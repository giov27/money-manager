package model

import anorm.SqlParser.get
import anorm.{RowParser, SQL, ~}
import play.api.db.DBApi
import play.api.libs.json.Json

import javax.inject.{Inject, Singleton}

case class User(
                 user_id: Int,
                 username: String,
                 password: String
               )

@Singleton
class UserDao @Inject()(DBApi: DBApi){
  implicit val userFormat = Json.format[User]
  private val db = DBApi.database("default")

  val user: RowParser[User] = {
    get[Int]("users_id") ~
      get[String]("username") ~
      get[String]("password") map {
      case user_id ~ username ~ password =>
        User(user_id, username, password)
    }
  }

  def getUserList(): (Boolean, String, List[User]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: List[User] = List()
    try {
      sqlQuery = SQL(
        """
        SELECT *
        FROM users
        ORDER BY users_id ASC;
        """)
        .as(user.*)
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def getUserByName(name: String): (Boolean, String, Option[User]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Option[User] = None
    try {
      sqlQuery = SQL(
        s"""
        SELECT *
        FROM users
        WHERE username = '${name}';
        """)
        .as(user.singleOpt)
      if (sqlQuery == None){
        throw new Exception
      }
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def postRegister(user: User): (Boolean, String, Option[Long]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Option[Long] = None
    try {
      sqlQuery = SQL(
        s"""
        INSERT INTO users (username, password)
        VALUES ('${user.username}', '${user.password}');
        """)
        .executeInsert()
      if (sqlQuery == None){
        throw new Exception
      }
      message = s"Berhasil mendaftarkan user"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }
}