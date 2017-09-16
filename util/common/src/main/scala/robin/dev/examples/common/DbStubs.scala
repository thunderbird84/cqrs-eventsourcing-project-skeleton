package robin.dev.examples.common

import java.sql.{Connection, DriverManager}

object DbStubs {
  val url = Configs.jdbcUrl
  val username = Configs.jdbcUsername
  val password = Configs.jdbcPassword

  Class.forName("com.mysql.jdbc.Driver")

  //TODO: Should using connection pool
  def getConnection(): Connection = DriverManager.getConnection(url, username, password)

  def usingConnection(f:(Connection)=> AnyRef): AnyRef ={
      val conn = getConnection()
      //try {
        f(conn)
//      } catch {
//         case t: Throwable => throw t
//      }
//      finally {
//        conn.close()
//      }
  }
}
