package controllers

import controllers.helpers.Cryptic
import model.{User, UserDao}
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.Inject

class UserController @Inject()(
                                val controllerComponents: ControllerComponents,
                                val userDao: UserDao,
                                val cryptic: Cryptic
                              ) extends BaseController {
  import userDao.userFormat

  def readUserList() = Action {
    val res: (Boolean, String, List[User])= userDao.getUserList()
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

  def readUserByUsername(name: String) = Action {
    val res: (Boolean, String, Option[User])= userDao.getUserByName(name)
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

  def login() = Action { implicit request: Request[AnyContent] =>
    val param = request.body.asJson.get
    var json = Json.obj()
    val username = (param \ "username" ).asOpt[String].getOrElse("-")
    val password = (param \ "password" ).asOpt[String].getOrElse("-")
    val res: (Boolean, String, Option[User])= userDao.getUserByName(username)
    try{
      if (res._3 != null && cryptic.checkPassword(password, res._3.get.password)){
        json = Json.obj(
          "metadata" -> Json.obj(
            "status" -> res._1,
            "message" -> res._2
          ),
          "res" -> Json.obj(
            "category_data" -> res._3
          )
        )
    }
    }catch {
      case e: Exception => {
        json = Json.obj(
          "metadata" -> Json.obj(
            "status" -> res._1,
            "message" -> res._2
          ),
          "res" -> Json.obj(
            "category_data" -> res._3
          )
        )
      }
    }
    Ok(json)
  }

  def register() = Action {implicit request: Request[AnyContent] =>
    val param = request.body.asJson.get
    val username = (param \ "username" ).asOpt[String].getOrElse("-")
    val password = (param \ "password" ).asOpt[String].getOrElse("-")
    val user = User(0, username, cryptic.createPassword(password))
    val res: (Boolean, String, Option[Long])= userDao.postRegister(user)
    val json = Json.obj(
      "metadata" -> Json.obj(
        "status" -> res._1,
        "message" -> res._2
      ),
      "res" -> Json.obj(
        "user_data_id" -> res._3
      )
    )
    Ok(json)
  }

}
