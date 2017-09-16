package robin.dev.examples.http

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, _}
import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory
import robin.dev.examples.common._
import robin.dev.examples.service.UserService


class UserRoute extends UserService {

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
                 user.validate() match {
                   case result if result.nonEmpty => complete(StatusCodes.custom(400, result.get, result.get))
                   case _ => getById(id).map(updateUser(_, user)) match {
                     case None => complete(StatusCodes.NotFound)
                     case Some(user) => complete(user)
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

  implicit class UserExt(u: User) {
    def validate(): Option[String] = {
      if (u.id.length > 0) {
        Some("user id must be empty string \"\"")
      } else if (EmaiRegx.findFirstIn(u.email).isEmpty) {
        Some("email is invalid")
      }else {
        None
      }
    }
  }

}