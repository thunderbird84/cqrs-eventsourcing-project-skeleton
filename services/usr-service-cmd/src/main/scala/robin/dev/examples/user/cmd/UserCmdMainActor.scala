package robin.dev.examples.user.cmd

import akka.actor.SupervisorStrategy.Resume
import akka.actor.{Actor, OneForOneStrategy, Props}
import org.slf4j.LoggerFactory
import robin.dev.examples.common.Configs
import robin.dev.examples.http.UserRoute.Out

class UserCmdMainActor(port: Int) extends Actor {

  private val log = LoggerFactory.getLogger(getClass)

  override val supervisorStrategy = OneForOneStrategy() {
    case e => log.error(s"Unexpected exception : ", e)
      Resume
  }
  override def receive: Receive = {
    case m: Out.UserChanges =>
            rabbitMq forward RabbitMq.In.SendMessage(RabbitMq.UserChangeRoutingKey, m.e)
    case o => log.error("Unknown message")
  }

  val rabbitMq =  context.actorOf(RabbitMq.props(Configs.amqpUri))

  val webServer = context.actorOf(WebServer.props(port))
}

object UserCmdMainActor {
  def props(port: Int) = Props(new UserCmdMainActor(port))
}