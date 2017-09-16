package robin.dev.examples.common

case class User(id: String,
                firstName: String,
                lastName: String,
                age: Int,
                email: String,
                secret: String)

case class UserActivity(id: String,
                        userId: String,
                        `type`: String,
                        time: Long,
                        browserAgent: String)

case class UserInfo(id: String,
                        firstName: String,
                        lastName: String,
                        age: Int,
                        activities: Seq[UserActivity])

case object Activity{
  val Login = "LOGIN"
  val ChangeEmail  = "CHANGE_EMAIL"
}