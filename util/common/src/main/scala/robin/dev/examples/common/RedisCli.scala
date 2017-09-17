package robin.dev.examples.common

import java.io.Closeable
import java.net.URI

import akka.actor.ActorSystem
import com.google.common.collect.ImmutableSet
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.{Jedis, JedisPool, JedisSentinelPool, Protocol}
import redis.clients.util.Pool
import RedisCli._
import scala.concurrent.Future

trait RedisCli {
  implicit val system: ActorSystem
  implicit lazy val ec = system.dispatcher

  def redisUrl: String
  def userAgent: String

  val indexPool = getIndexPool(redisUrl, 0)

  def getIndexPool(configUrl: String, db: Int): Pool[Jedis] = {
    val url: URI = URI.create(configUrl)
    url.getScheme match {
      case "redis-sentinel" =>
        val Array(_, master) = url.getPath.split("/")
        new JedisSentinelPool(master, ImmutableSet.of(s"${url.getHost}:${url.getPort}"), new GenericObjectPoolConfig(), Protocol.DEFAULT_TIMEOUT, null, db, null)
      case "redis" =>
        val Array(_) = url.getPath.split("/")
        new JedisPool(new GenericObjectPoolConfig(), url.getHost, url.getPort, Protocol.DEFAULT_TIMEOUT, null, db, null)
    }
  }

  def execute[T](pool: Pool[Jedis])(op: Jedis => T): Future[T] = {
    Future {
      pool.getResource.using { jedis: Jedis =>
        op(jedis)
      }
    }
  }

  def getPool(modelName: String): Future[Pool[Jedis]] = {
    execute(indexPool) { jedis =>
      var dbIndex = jedis.get(modelName)

      if (dbIndex == null) {
        dbIndex = jedis.incr(DB_INDEX).toString
        jedis.set(modelName, dbIndex)
      }

      getIndexPool(redisUrl, dbIndex.toInt)
    }
  }

  def healthCheck(): Future[String] = {
    execute(indexPool) { jedis =>
      jedis.ping()
    }
  }
}

object RedisCli {
  val DB_INDEX = "db_index"
  val USER_INFO = "user_info"

  implicit class RichCloseable[T <: Closeable](val underlying: T) extends AnyVal {
    def using[U](f: (T) => U): U =
      try f(underlying)
      finally underlying.close()
  }
}