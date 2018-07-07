package controllers

import javax.inject.Inject
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import play.api.mvc._
import resources._

class ShortenerController @Inject()(cc: ControllerComponents, resourceHandler: UrlResourceHandler)
  extends AbstractController(cc) {

  def createShortURL: Action[AnyContent] = Action { implicit request =>
    val body: AnyContent = request.body

    body.asJson match {
      case None => BadRequest("Expecting application/json request body")
      case Some(jsonBody) => decode[UrlResource](jsonBody.toString()) match {
        case Left(error)=> BadRequest("Invalid application/json request body. Reason: " + error.toString)
        case Right(resource) => resourceHandler.createShortURL(resource) match {
          case None => Conflict("There was a problem encoding the URL")
          case Some(shortUrl) => Created(shortUrl.asJson.noSpaces).as("application/json")
        }
      }
    }
  }

  def redirect(short_url: String) = Action {
    resourceHandler.lookup(short_url) match {
      case None => NotFound
      case Some(originalUrl) => MovedPermanently(originalUrl)
        .withHeaders(CACHE_CONTROL -> "no-cache, no-store, max-age=0, must-revalidate")
    }
  }

}
