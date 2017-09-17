package robin.dev.examples.user.compound

import akka.actor.{Actor, Props}
import org.slf4j.LoggerFactory
import robin.dev.examples.common.Configs


class UserCompoundMainActor(port: Int) extends Actor{

  private implicit val log = LoggerFactory.getLogger(getClass)

  def receive: Receive = {
    case o => log.error("Unknown message")
  }

  val rabbitMq = context.actorOf(RabbitMq.props(Configs.amqpUri))
  val webServer = context.actorOf(WebServer.props(port))

}

object UserCompoundMainActor{

  def props(port: Int) = Props(new UserCompoundMainActor(port))
}