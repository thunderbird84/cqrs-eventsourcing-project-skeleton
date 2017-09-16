package robin.dev.examples.service

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import robin.dev.examples.common._
import spray.json.DefaultJsonProtocol

// TODO: should create very generic DAO
trait UserService extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val userFormat = jsonFormat6(User)
  implicit val userActivityFormat = jsonFormat5(UserActivity)
  implicit val userInfoFormat = jsonFormat5(UserInfo)

  def getByEmail(email: String): Option[User] =
    DbStubs.usingConnection(conn => {
      val sql = "SELECT *  FROM users WHERE email =? "
      val preparedStmt= conn.prepareStatement(sql)
      preparedStmt.setString(1, email)

      val rs = preparedStmt.executeQuery()
      val user: Option[User] = rs.next() match {
        case false => None
        case _ => Some(User(rs.getString("id"),
          rs.getString("first_name"),
          rs.getString("last_name"),
          rs.getInt("age"),
          rs.getString("email"),
          rs.getString("secret")))
      }
      preparedStmt.close()
      user
    }).asInstanceOf[Option[User]]


  def getById(id: String): Option[User] =
    DbStubs.usingConnection(conn => {

      val sql = "SELECT *  FROM users WHERE id =?"
      val preparedStmt= conn.prepareStatement(sql)
      preparedStmt.setString(1, id)

      val rs = preparedStmt.executeQuery()
      val user: Option[User] = rs.next() match {
        case false => None
        case _ => Some(User(rs.getString("id"),
          rs.getString("first_name"),
          rs.getString("last_name"),
          rs.getInt("age"),
          rs.getString("email"),
          rs.getString("secret")))
      }
      preparedStmt.close()

      user

    }).asInstanceOf[Option[User]]


  def createUser(entity: User): User =
    DbStubs.usingConnection(conn => {
      val id =GuidUtil.generateUUID
      val password = Md5.hash(entity.secret)
      val sql = "INSERT INTO users(id,first_name,last_name,age,email,secret) " +
        "VALUES(?,?,?,?,?,?)"
      val preparedStmt= conn.prepareStatement(sql)
      preparedStmt.setString(1, id)
      preparedStmt.setString(2, entity.firstName)
      preparedStmt.setString(3, entity.lastName)
      preparedStmt.setInt(4, entity.age)
      preparedStmt.setString(5, entity.email)
      preparedStmt.setString(6, password)
      preparedStmt.execute()
      preparedStmt.close()
      entity.copy(id = id)
    }).asInstanceOf[User]

  def updateUser(existed: User, entity: User): User =
    DbStubs.usingConnection(conn => {

      val password = entity.secret.trim() match {
        case "" => existed.secret
        case x => Md5.hash(x)
      }

      val sql = "UPDATE users SET first_name = ?," +
                                  "last_name =?," +
                                    "age =?, " +
                                    "email=?, " +
                                    "secret =? " +
                                    " WHERE id =?"

      val preparedStmt= conn.prepareStatement(sql)
      preparedStmt.setString(1, entity.firstName)
      preparedStmt.setString(2, entity.lastName)
      preparedStmt.setInt(3, entity.age)
      preparedStmt.setString(4, entity.email)
      preparedStmt.setString(5, password)
      preparedStmt.setString(6, existed.id)
      preparedStmt.executeUpdate()
      preparedStmt.close()
      entity.copy(secret = password)
    }).asInstanceOf[User]
}
