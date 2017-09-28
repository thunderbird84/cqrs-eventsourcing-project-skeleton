package robin.dev.examples.user.compound

import akka.actor.ActorSystem
import akka.actor.{Actor, Props}
import org.slf4j.LoggerFactory
import robin.dev.examples.common.Configs


class UserCompoundMainActor(port: Int) extends Actor{

  private implicit val log = LoggerFactory.getLogger(getClass)
  implicit val system: ActorSystem = context.system

  def receive: Receive = {
    case m: RabbitMq.Out.OutEvent  => storageActor forward m.e
    case o => log.error("Unknown message")
  }

  val redisCli = new RedisCliStubs(Configs.redisUrl,"usr-compound")
  val storageActor = context.actorOf(UserStorageActor.props(redisCli))

  val rabbitMq = context.actorOf(RabbitMq.props(Configs.amqpUri))
  val webServer = context.actorOf(WebServer.props(port))

}

object UserCompoundMainActor{

  def props(port: Int) = Props(new UserCompoundMainActor(port))
}