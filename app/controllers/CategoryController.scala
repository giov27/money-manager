package controllers

import model.{Category, CategoryDao}
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.Inject

class CategoryController @Inject()(
                                  val controllerComponents: ControllerComponents,
                                  val categoryDao: CategoryDao
                                  )extends BaseController{
  import categoryDao.categoryFormat

  def readCategoryList() = Action {
    val res: (Boolean, String, List[Category])= categoryDao.getCategoryList()
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
      "res" -> Json.obj(
        "category_data" -> res._3
      )
    )
    Ok(json)
  }

  def readCategoryById(id: Int) = Action {
    val res: (Boolean, String, Option[Category])= categoryDao.getCategoryById(id)
//    IF res_3 == null, redirect to 404(soon)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
      "res" -> Json.obj(
        "category_data" -> res._3
      )
    )
    Ok(json)
  }

  def readCategoryBySearch(name: String) = Action {
    val res: (Boolean, String, List[Category])= categoryDao.getCategoryBySearch(name)
    //    IF res_3 == null, redirect to 404(soon)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
      "res" -> Json.obj(
        "category_data" -> res._3
      )
    )
    Ok(json)
  }

  def deleteCategoryById(id: Int) = Action {
    val res: (Boolean, String, Int)= categoryDao.delCategoryById(id)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
    )
    Ok(json)
  }

  def updateCategoryById(id: Int) = Action { implicit request: Request[AnyContent] =>
    val param = request.body.asJson.get
//    val id = (param \ "categoryId" ).asOpt[Int].getOrElse(-1)
    val name = (param \ "categoryName" ).asOpt[String].getOrElse("-")
    val icon = (param \ "categoryIcon" ).asOpt[String].getOrElse("-") //isi default icon
    val category = Category(id, name, icon)
    val res: (Boolean, String, Int)= categoryDao.putCategoryById(category)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
    )
    Ok(json)
  }

  def createCategory() = Action { implicit request: Request[AnyContent] =>
    val param = request.body.asJson.get
    val name = (param \ "categoryName" ).asOpt[String].getOrElse("-")
    val icon = (param \ "categoryIcon" ).asOpt[String].getOrElse("-") //isi default icon
//    val category = Category(id, name, icon)
    val res: (Boolean, String, Option[Long])= categoryDao.postCategory(name, icon)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
      "res" -> Json.obj(
        "category_data" -> res._3
      )
    )
    Ok(json)
  }
}
