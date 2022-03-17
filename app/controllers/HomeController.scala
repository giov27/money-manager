package controllers

import javax.inject._
import play.api._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
                                controllerComponents: ControllerComponents,
                              ) extends AbstractController(controllerComponents) {
  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def overview() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.overview())
  }

  def landing() = Action { implicit request: Request[AnyContent] =>
    val sessionOpt = request.session.get("connected")
    sessionOpt.map{ session =>
      Ok(views.html.landing())
    }.getOrElse(
      Redirect(routes.HomeController.login("Please login first!"))
    )
  }

  def add() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.add())
  }

  def edit() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.edit())
  }

  def login(message: String) = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(message))
  }


  def register() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.register())
  }
}
