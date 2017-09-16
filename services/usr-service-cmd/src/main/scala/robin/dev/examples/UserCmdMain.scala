package robin.dev.examples

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory
import robin.dev.examples.user.cmd.UserCmdMainActor

class UserCmdMain(port: Int) {
  //  SLF4JBridgeHandler.removeHandlersForRootLogger()
  //  SLF4JBridgeHandler.install()
  //  DefaultExports.initialize()

  private implicit val log = LoggerFactory.getLogger(getClass)

  val serviceName = "user-cmd"
  implicit val system = ActorSystem(serviceName)

  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher



  val appActor = system.actorOf(UserCmdMainActor.props(port))

  log.info(s"Starting service $serviceName")

  sys.addShutdownHook {
    log.warn(s"Shutting down $serviceName")
    system.terminate()
  }
}

object UserCmdMain {
  def main(args: Array[String]): Unit = {
    new UserCmdMain(80)
  }
}

object UserCmdMainDev {
  def main(args: Array[String]): Unit = {
    new UserCmdMain(8402)
  }
}