package controllers

import model.{Category, CategoryDao, Ledger, LedgerDao}
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.Inject
import scala.util.control.Breaks.break

class LedgerController @Inject()(
                                val controllerComponents: ControllerComponents,
                                val ledgerDao: LedgerDao,
                                val categoryDao: CategoryDao,
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
    val title = (param \ "title").asOpt[String].getOrElse("-")
    val amount = (param \ "amount").asOpt[String].getOrElse("").toDouble
    val note = (param \ "note").asOpt[String].getOrElse("-")
    val category_data = (param \ "category_id").asOpt[Long].getOrElse(
      (param \ "category_id").as[String]
    )
    var category_id: Long = 0
    if(category_data.asInstanceOf[String].trim() == ""){
      category_id = -1
    }else if(category_data.isInstanceOf[String]){
      val searchId : (Boolean, String, Option[Category]) = categoryDao.getCategoryByName(category_data.asInstanceOf[String])
      if(searchId._3 != None){
        category_id = searchId._3.get.category_id
      }else{
        val postCategory: (Boolean, String, Option[Long])= categoryDao.postCategory(category_data.asInstanceOf[String],"fas fa-question")
        category_id = postCategory._3 match {
          case None => -1
          case Some(id: Long) => id
        }
        if(category_id == -1){
          val json = Json.obj(
            "metadata" -> Json.obj(
              "status" -> postCategory._1,
              "message" -> postCategory._2
            )
          )
          Ok(json)
          break;
        }

      }
    }else{
      category_id = category_data.asInstanceOf[Long]
    }

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
    val title = (param \ "title").asOpt[String].getOrElse("-")
    val amount = (param \ "amount").asOpt[String].getOrElse("").toDouble
    val note = (param \ "note").asOpt[String].getOrElse("-")
    val category_data = (param \ "category_id").asOpt[Long].getOrElse(
      (param \ "category_id").as[String]
    )
    var category_id: Long = 0
    if(category_data.asInstanceOf[String].trim() == ""){
      category_id = -1
    }else if(category_data.isInstanceOf[String]){
      val searchId : (Boolean, String, Option[Category]) = categoryDao.getCategoryByName(category_data.asInstanceOf[String])
      if(searchId._3 != None ){
        category_id = searchId._3.get.category_id
      }else{
        val postCategory: (Boolean, String, Option[Long])= categoryDao.postCategory(category_data.asInstanceOf[String],"fas fa-question")
        category_id = postCategory._3 match {
          case None => -1
          case Some(id: Long) => id
        }
        if(category_id == -1){
          val json = Json.obj(
            "metadata" -> Json.obj(
              "status" -> postCategory._1,
              "message" -> postCategory._2
            )
          )
          Ok(json)
          break
        }

      }
    }else{
      category_id = category_data.asInstanceOf[Long]
    }

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
