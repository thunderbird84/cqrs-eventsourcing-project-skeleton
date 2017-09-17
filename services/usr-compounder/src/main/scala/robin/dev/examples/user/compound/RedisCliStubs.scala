package robin.dev.examples.user.compound

import akka.actor.ActorSystem
import robin.dev.examples.common.RedisCli


class RedisCliStubs(override val redisUrl: String, override val userAgent: String)
                   (implicit val system: ActorSystem) extends RedisCli {
}

