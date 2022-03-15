package controllers

import model.{Category, CategoryDao, Ledger, LedgerDao}
import play.api.libs.json.Json
import play.api.mvc.{BaseController, ControllerComponents}

import javax.inject.Inject

class OverviewController @Inject()(
                                val controllerComponents: ControllerComponents,
                                val ledgerDao: LedgerDao,
                                val categoryDao: CategoryDao,
                                )extends BaseController{

  import ledgerDao.ledgerFormat

  def readLedgerByIntervalDate() = Action {
    val res: (Boolean, String, List[Ledger])= ledgerDao.getLedgerOverviewByWeek("qw","2")
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
}
