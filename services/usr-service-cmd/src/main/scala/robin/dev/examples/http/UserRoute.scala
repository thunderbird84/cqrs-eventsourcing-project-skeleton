package robin.dev.examples.http

import akka.actor.ActorRef
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, _}
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory
import robin.dev.examples.common._
import robin.dev.examples.service.UserService


class UserRoute(mainActor: ActorRef) extends UserService {

  import UserRoute._

  private val log = LoggerFactory.getLogger(getClass)

  def route(): Route = pathPrefix("users") {
    pathEndOrSingleSlash {
      post {
        entity(as[User]) { user =>
          log.info(s"create user $user")
          val result = user.validate()
          if (result.nonEmpty) {
            complete(StatusCodes.custom(400, result.get, result.get))
          }

          getByEmail(user.email) match {
            case Some(_) => complete(StatusCodes.custom(400, "user already exist", "user already exist"))
            case None =>
              val created = createUser(user)
              mainActor ! Out.UserChanges(UserChangesEvent("user.created", user.id))
              log.info(s"user created $created")
              complete(created)
          }
        }
      }
    } ~
    path(Segment) { id: String =>
      pathEnd {
        put {
          entity(as[User]) { user =>
             user.id.equals(id) match {
               case false =>
                    log.info(s"equals ${!user.id.equals(id)}")
                    complete(StatusCodes.custom(400, "wrong id", "wrong id"))
               case _ =>
                 user.validate(false) match {
                   case result if result.nonEmpty => complete(StatusCodes.custom(400, result.get, result.get))
                   case _ => getById(id).map(existing => {
                       if (existing.emailChanged(user)) {
                         mainActor ! Out.UserChanges(UserChangesEvent(Activity.ChangeEmail, existing.id))
                       }
                       updateUser(existing, user)
                     }) match {
                       case None => complete(StatusCodes.NotFound)
                       case Some(user) =>
                                      mainActor ! Out.UserChanges(UserChangesEvent("user.updated", user.id))
                                     complete(user)
                     }
                 }
            }
          }
        }
      }
    }
  }
}

object UserRoute {

  val EmaiRegx = """(?=[^\s]+)(?=(\w+)@([\w\.]+))""".r

  case object Out{
    case class UserChanges(e: UserChangesEvent)
  }
  implicit class UserExt(u: User) {
    def validate(forCreated: Boolean = true): Option[String] = {
      if (u.id.length > 0 && forCreated) {
        Some("user id must be empty string \"\"")
      } else if (EmaiRegx.findFirstIn(u.email).isEmpty) {
        Some("email is invalid")
      }else {
        None
      }
    }

    def emailChanged(that: User): Boolean = !u.email.equals(that.email)
  }

}