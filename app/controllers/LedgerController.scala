package controllers

import model.{Ledger, LedgerDao}
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.Inject

class LedgerController @Inject()(
                                val controllerComponents: ControllerComponents,
                                val ledgerDao: LedgerDao
                                )extends BaseController{

  import ledgerDao.ledgerFormat

  def readLedgerList() = Action {
    val res: (Boolean, String, List[Ledger])= ledgerDao.getLedgerList()
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
      "res" -> Json.obj(
        "ledger_data" -> res._3
      )
    )
    Ok(json)
  }

  def readLedgerListByDate(transaction_date: String) = Action {
    val res: (Boolean, String, List[Ledger])= ledgerDao.getLedgerListByDate(transaction_date)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
      "res" -> Json.obj(
        "ledger_data" -> res._3
      )
    )
    Ok(json)
  }

  def readLedgerById(id: Int) = Action {
    val res: (Boolean, String, Option[Ledger])= ledgerDao.getLedgerById(id)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
      "res" -> Json.obj(
        "ledger_data" -> res._3
      )
    )
    Ok(json)
  }

  def deleteLedgerById(id: Int) = Action {
    val res: (Boolean, String, Int)= ledgerDao.delLedgerById(id)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      )
    )
    Ok(json)
  }

  def updateLedgerById(id: Int) = Action { implicit request: Request[AnyContent] =>
    val param = request.body.asJson.get
    val transaction_date = (param \ "transaction_date" ).asOpt[String].getOrElse("-")
    val transaction_type = (param \ "transaction_type" ).asOpt[String].getOrElse("-") //isi default icon
    val category_id = (param \ "category_id").asOpt[Int].getOrElse(-1)
    val title = (param \ "title").asOpt[String].getOrElse("-")
    val amount = (param \ "amount").asOpt[String].getOrElse("-").toDouble
    val note = (param \ "note").asOpt[String].getOrElse("-")
    val ledger = Ledger(id, transaction_date, transaction_type, category_id, title, amount, note, null)
    val res: (Boolean, String, Int)= ledgerDao.putLedgerById(ledger)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      )
    )
    Ok(json)
  }

  def createLedger() = Action { implicit request: Request[AnyContent] =>
    val param = request.body.asJson.get
    val transaction_date = (param \ "transaction_date" ).asOpt[String].getOrElse("-")
    val transaction_type = (param \ "transaction_type" ).asOpt[String].getOrElse("-") //isi default icon
    val category_id = (param \ "category_id").asOpt[Int].getOrElse(-1)
    val title = (param \ "title").asOpt[String].getOrElse("-")
    val amount = (param \ "amount").asOpt[String].getOrElse("").toDouble
    val note = (param \ "note").asOpt[String].getOrElse("-")
    val ledger = Ledger(0, transaction_date, transaction_type, category_id, title, amount, note, null)
    val res: (Boolean, String, Option[Long])= ledgerDao.postLedgerById(ledger)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      )
    )
    Ok(json)
  }

  def readIncomeByDate(transaction_date: String, transaction_type: String) = Action { implicit request: Request[AnyContent] =>
    val res: (Boolean, String, Option[Double])= ledgerDao.getCountByDate(transaction_date, transaction_type)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
      "res" -> Json.obj(
        "income_data" -> res._3
      )
    )
    Ok(json)
  }

  def readExpenseByDate(transaction_date: String, transaction_type: String) = Action { implicit request: Request[AnyContent] =>
    val res: (Boolean, String, Option[Double])= ledgerDao.getCountByDate(transaction_date, transaction_type)
    val res._3 = if(res._3 == null) 0 else res._3
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
      "res" -> Json.obj(
        "expense_data" -> res._3
      )
    )
    Ok(json)
  }
}
