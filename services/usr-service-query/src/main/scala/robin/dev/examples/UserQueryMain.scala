package robin.dev.examples

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory
import robin.dev.examples.user.query.WebServer

class UserQueryMain(port: Int) {

//  SLF4JBridgeHandler.removeHandlersForRootLogger()
//  SLF4JBridgeHandler.install()
//  DefaultExports.initialize()

  private implicit val log = LoggerFactory.getLogger(getClass)

  val serviceName = "user-query"
  implicit val system = ActorSystem(serviceName)

  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher



  val appActor = system.actorOf(WebServer.props(port))

  log.info(s"Starting service $serviceName")

  sys.addShutdownHook {
    log.warn(s"Shutting down $serviceName")
    Http().shutdownAllConnectionPools().onComplete {
      case _ => system.terminate()
    }
  }

}

object UserQueryMain {
  def main(args: Array[String]): Unit = {
    new UserQueryMain(80)
  }
}
object UserQueryMainDev {
  def main(args: Array[String]): Unit = {
    new UserQueryMain(8401)
  }
}