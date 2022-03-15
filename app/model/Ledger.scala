package model

import anorm.SqlParser.{get, scalar}
import anorm.{RowParser, SQL, ~}
import play.api.db.DBApi
import play.api.libs.json.Json

import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.{Inject, Singleton}

case class Ledger(
                   ledger_id: Int,
                   transaction_date: String,
                   transaction_type: String,
                   category_id : Long,
                   title: String,
                   amount: Double,
                   note: String,
                   category: Option[Category]
                 )

@Singleton
class LedgerDao @Inject()(DBApi: DBApi, categoryDao: CategoryDao) {

  import categoryDao.categoryFormat
  implicit val ledgerFormat = Json.format[Ledger]
  private val db = DBApi.database("default")
  val dateFormat =  new SimpleDateFormat("dd/MM/YY")

  val ledger: RowParser[Ledger] = {
    get[Int]("ledger_id") ~
      get[Date]("transaction_date") ~
      get[String]("transaction_type") ~
      get[Int]("category_id") ~
      get[String]("title") ~
      get[Double]("amount") ~
      get[String]("note") ~
      categoryDao.category.? map {
      case ledger_id ~ transaction_date ~ transaction_type ~ category_id ~ title ~ amount ~ note ~ category =>
        Ledger(ledger_id, dateFormat.format(transaction_date), transaction_type, category_id, title, amount, note, category)
    }
  }

  val countParser: RowParser[Double] = {
    get[Double]("sum") map {
      case sum => (sum)
    }
  }

  def getLedgerList(): (Boolean, String, List[Ledger]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: List[Ledger] = List()
    try {
      sqlQuery = SQL(
        """
        SELECT *
        FROM ledger l
        JOIN category c
        ON l.category_id = c.category_id
        ORDER BY ledger_id ASC;
        """)
        .as(ledger.*)
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def getLedgerListByDate(transaction_date:String): (Boolean, String, List[Ledger]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: List[Ledger] = List()
    try {
      sqlQuery = SQL(
        s"""
        SELECT *
        FROM ledger l
        JOIN category c
        ON l.category_id = c.category_id
        WHERE transaction_date = '${transaction_date}'
        ORDER BY ledger_id ASC;
        """)
        .as(ledger.*)
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def getLedgerById(id: Int): (Boolean, String, Option[Ledger]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Option[Ledger] = None
    try {
      sqlQuery = SQL(
        s"""
        SELECT *
        FROM ledger l
        JOIN category c
        ON l.category_id = c.category_id
        WHERE l.ledger_id = ${id}
        """)
        .as(ledger.singleOpt)
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def delLedgerById(id: Int): (Boolean, String, Int) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Int = 0
    try {
      sqlQuery = SQL(
        s"""
        DELETE FROM ledger
        WHERE ledger_id = ${id}
        """)
        .executeUpdate()
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def putLedgerById(ledger: Ledger): (Boolean, String, Int) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Int = 0
    try {
      sqlQuery = SQL(
        s"""
        UPDATE ledger
        SET ledger_id = ${ledger.ledger_id},
          transaction_date = '${ledger.transaction_date}',
          transaction_type = '${ledger.transaction_type}',
          category_id = '${ledger.category_id}',
          title= '${ledger.title}',
          amount= ${ledger.amount},
          note= '${ledger.note}'
        WHERE ledger_id = ${ledger.ledger_id}
        """)
        .executeUpdate()
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def postLedgerById(ledger: Ledger): (Boolean, String, Option[Long]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Option[Long] = None
    try {
      sqlQuery = SQL(
        s"""
        INSERT INTO ledger
        (transaction_date, transaction_type,
        category_id, title,
        amount, note)
        VALUES(
        '${ledger.transaction_date}', '${ledger.transaction_type}',
        '${ledger.category_id}', '${ledger.title}',
        '${ledger.amount}', '${ledger.note}'
        )
        """)
        .executeInsert()
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def getCountByDate(transaction_date:String, transaction_type:String): (Boolean, String, Option[Double]) = db.withConnection { implicit c =>
    // create a SqlQuery for all of the "select all" methods
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: Option[Double] = None
    try {
      sqlQuery = SQL(
        s"""
        SELECT SUM(amount)
        FROM ledger
        WHERE transaction_date = '${transaction_date}' AND transaction_type = '${transaction_type}';
        """)
        .as(countParser.singleOpt)
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: anorm.AnormException =>{
        message = s"Berhasil mendapatkan data"
        sqlQuery = Some(0)
      }
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
        sqlQuery = Some(0)
      }
    }
    (status, message, sqlQuery)
  }

  def getLedgerOverviewByWeek(transaction_date:String, transaction_type:String):(Boolean, String, List[Ledger]) = db.withConnection{implicit c =>
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: List[Ledger] = List()
    try {
      sqlQuery = SQL(
        s"""
          SELECT *
          FROM ledger l
          JOIN category c
          ON l.category_id = c.category_id
          WHERE transaction_date BETWEEN '2022-03-06' AND '2022-03-15';
        """)
        .as(ledger.*)
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }

  def getLedgerCountByWeek():(Boolean, String, List[Ledger]) = db.withConnection{implicit c =>
    var status: Boolean = true
    var message: String = ""
    var sqlQuery: List[Ledger] = List()
    try {
      sqlQuery = SQL(
        s"""
          SELECT *
          FROM ledger l
          JOIN category c
          ON l.category_id = c.category_id
          WHERE transaction_date BETWEEN '2022-03-06' AND '2022-03-15';
        """)
        .as(ledger.*)
      message = s"Berhasil mendapatkan data"
    } catch {
      case e: Exception => {
        status = false
        message = "Gagal mendapatkan data: err = " + e.toString
      }
    }
    (status, message, sqlQuery)
  }
}
