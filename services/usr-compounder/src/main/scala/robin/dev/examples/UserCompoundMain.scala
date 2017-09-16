package robin.dev.examples

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.slf4j.LoggerFactory
import robin.dev.examples.user.compound.UserCompoundMainActor

class UserCompoundMain(port: Int) {
  //  SLF4JBridgeHandler.removeHandlersForRootLogger()
  //  SLF4JBridgeHandler.install()
  //  DefaultExports.initialize()

  private implicit val log = LoggerFactory.getLogger(getClass)

  val serviceName = "user-compound"
  implicit val system = ActorSystem(serviceName)

  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher



  val appActor = system.actorOf(UserCompoundMainActor.props(port))


  log.info(s"Starting service $serviceName")

  sys.addShutdownHook {
    log.warn(s"Shutting down $serviceName")
    system terminate()
  }
}

object UserCompoundMain {
  def main(args: Array[String]): Unit = {
    new UserCompoundMain(80)
  }
}

object UserCompoundMainDev {
  def main(args: Array[String]): Unit = {
    //println("http://localhost:8403")
    new UserCompoundMain(8403)
  }
}