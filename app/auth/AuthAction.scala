//package auth
//
//import pdi.jwt.JwtClaim
//import play.api.http.HeaderNames
//import play.api.mvc._
//
//import javax.inject.Inject
//import scala.concurrent.{ExecutionContext, Future}
//import scala.util.{Failure, Success}
//
//
//// A custom request type to hold our JWT claims, we can pass these on to the
//// handling action
//case class UserRequest[A](jwt: JwtClaim, token: String, request: Request[A]) extends WrappedRequest[A](request)
//
//class AuthAction @Inject()(
//                          bodyParser: BodyParsers.Default,
//                          authService: AuthService
//                          )(
//                          implicit ec: ExecutionContext
//) extends ActionBuilder [UserRequest ,AnyContent ]{
//
//  override def parser: BodyParser[AnyContent] = bodyParser
//
//  override protected def executionContext: ExecutionContext = ec
//
//  // A regex for parsing the Authorization header value
//  private val headerTokenRegex = """Bearer (.+?)""".r
//
//  // Called when a request is invoked. We should validate the bearer token here
//  // and allow the request to proceed if it is valid.
//  override def invokeBlock[A](request: Request[A], block: UserRequest[A] => Future[Result]): Future[Result] =
//    extractBearerToken(request) map { token =>
//      authService.validateJwt(token) match {
//        case Success(claim) => block(UserRequest(claim, token, request))      // token was valid - proceed!
//        case Failure(t) => Future.successful(Results.Unauthorized(t.getMessage))  // token was invalid - return 401
//      }
//    } getOrElse Future.successful(Results.Unauthorized)     // no token was sent - return 401
//
//
//  private def extractBearerToken[A](request: Request[A]): Option[String] =
//    request.headers.get(HeaderNames.AUTHORIZATION) collect {
//      case headerTokenRegex(token) => token
//    }
//
////  Source: https://auth0.com/blog/build-and-secure-a-scala-play-framework-api/
////  Our implementation is fairly simple. We first extract the Authorization header from the request
//  //  and retrieve the token from the header value. We then use our AuthService.valiateJwt method to
//  //  validate the token and extract the claims. If we successfully manage to get the claims, then the
//  //  token has been validated and we should continue with the request. Otherwise, if there was a failure
//  //  or some other reason why we could not retrieve the claims, then an Unauthorized result is returned.
//  //  Similarly, if no token was present in the request or the header was missing, then we likewise return
//  //  Unauthorized.
//
//
//}
