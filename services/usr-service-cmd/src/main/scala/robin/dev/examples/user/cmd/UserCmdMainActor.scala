package robin.dev.examples.user.cmd

import akka.actor.{Actor, Props}
import org.slf4j.LoggerFactory

class UserCmdMainActor(port: Int) extends Actor {

  private val log = LoggerFactory.getLogger(getClass)

  override def receive: Receive = {
    case o => log.error("Unknown message")
  }


  val webServer = context.actorOf(WebServer.props(port))
}

object UserCmdMainActor {
  def props(port: Int) = Props(new UserCmdMainActor(port))
}