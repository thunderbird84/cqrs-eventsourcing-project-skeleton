package robin.dev.examples.common

import java.net.URI

import com.typesafe.config.ConfigFactory

object Configs {
  val env: String = Option(System.getProperty("env")).getOrElse("dev")

  val config = ConfigFactory.load("conf/" + env + ".conf")

  def amqUri: URI = URI.create(config.getString("qpid.uri"))

  def redisHost: String = config.getString("redis.host")
  def redisPort: Int = config.getInt("redis.port")

  def jdbcUrl: String = config.getString("jdbc.url")
  def jdbcUsername: String = config.getString("jdbc.username")
  def jdbcPassword: String = config.getString("jdbc.password")
}
