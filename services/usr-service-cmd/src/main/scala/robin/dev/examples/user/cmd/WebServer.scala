package robin.dev.examples.user.cmd

import akka.Done
import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.http.scaladsl.settings.ServerSettings
import robin.dev.examples.http.UserRoute

import scala.concurrent.{ExecutionContext, Future, Promise}


object WebServer {

  def props(port: Int)
  = Props(new WebServer(port))

  class WebServer(port: Int) extends HttpApp with Actor {

    override def receive = {
      case _ =>
    }

    override def routes: Route =
      pathSingleSlash {
        get {
          complete("user-cmd.robin.examples works!")
        }
      } ~
      path("healthcheck") {
        get {
          complete("healthy!")
        }
      } ~
      new UserRoute().route()

    override def waitForShutdownSignal(actorSystem: ActorSystem)(implicit executionContext: ExecutionContext): Future[Done] = {
      val promise = Promise[Done]()
      sys.addShutdownHook {
        promise.trySuccess(Done)
      }
      promise.future
    }

    // Starting the server
    startServer("0.0.0.0", port, ServerSettings(context.system), context.system)
  }

}