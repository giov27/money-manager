package model

import anorm.SqlParser.get
import anorm.{RowParser, SQL, ~}
import play.api.db.DBApi
import play.api.libs.json.Json

import javax.inject.{Inject, Singleton}

case class Category(
                     category_id: Int,
                     name: String,
                     icon: String
                   )

@Singleton
class CategoryDao @Inject()(DBApi: DBApi) {
  implicit val categoryFormat = Json.format[Category]
  private val db = DBApi.database("default")

  val category: RowParser[Category] = {
    get[Int]("category_id") ~
      get[String]("name") ~
      get[String]("icon") map {
      case category_id ~ name ~ icon => Category(category_id, name, icon)
    }
  }

  def getCategoryList(): (Boolean, String, List[Category]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: List[Category] = List()
    try {
      sqlQuery = SQL(
        """
        SELECT *
        FROM category
        ORDER BY category_id ASC
        """)
        .as(category.*)
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def getCategoryById(category_id: Int): (Boolean, String, Option[Category]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Option[Category] = None
    try {
      sqlQuery = SQL(
        s"""
        SELECT *
        FROM category
        WHERE category_id = $category_id
        """)
        .as(category.singleOpt)
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def getCategoryByName(category_name: String): (Boolean, String, Option[Category]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Option[Category] = None
    try {
      sqlQuery = SQL(
        s"""
        SELECT *
        FROM category
        WHERE name = '$category_name'
        """)
        .as(category.singleOpt)
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def getCategoryBySearch(category_name: String): (Boolean, String, List[Category]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: List[Category] = List()
    try {
      sqlQuery = SQL(
        s"""
        SELECT *
        FROM category
        WHERE name LIKE '$category_name%'
        """)
        .as(category.*)
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def delCategoryById(category_id: Int): (Boolean, String, Int) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Int = 0
    try {
      sqlQuery = SQL(
        s"""
        DELETE FROM category
        WHERE category_id = $category_id
        """)
        .executeUpdate()
      message = s"Berhasil menghapus data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal menghapus data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def putCategoryById(category: Category): (Boolean, String, Int) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Int = 0
    try {
      sqlQuery = SQL(
        s"""UPDATE category
                        SET category_id = ${category.category_id},
                          name = '${category.name}',
                          icon = '${category.icon}'
                        WHERE category_id = ${category.category_id};
                        """.stripMargin)
        .executeUpdate()
      message = s"Berhasil mengupdate data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal menghapus data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def postCategory(name: String, icon: String): (Boolean, String, Option[Long]) = db.withConnection{ implicit c =>
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Option[Long] = None
    try{
      sqlQuery = SQL(
        s"""
           |INSERT INTO category
           |(name, icon)
           |VALUES
           |('${name}', '${icon}')
           |RETURNING category_id;
           |""".stripMargin
      ).executeInsert()
      message = s"Berhasil menambahkan data"
    }catch {
      case e: Exception => {
        status = false
        message = "Gagal menambahkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }
}
