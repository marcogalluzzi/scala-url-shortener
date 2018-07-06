package controllers

import javax.inject.Inject
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import play.api.libs.json.JsValue
import play.api.mvc._
import resources._

class ShortenerController @Inject()(cc: ControllerComponents, resourceHandler: UrlResourceHandler)
  extends AbstractController(cc) {

  def createShortURL: Action[AnyContent] = Action { implicit request =>
    val body: AnyContent = request.body
    val jsonBody: Option[JsValue] = body.asJson

    if (jsonBody.isDefined) {
      val eitherErrorOrData = decode[UrlResource](jsonBody.get.toString())
      if (eitherErrorOrData.isRight) {
        val resource: UrlResource = eitherErrorOrData.right.get
        val shortUrl = resourceHandler.createShortURL(resource)
        if (shortUrl.isDefined) {
          Created(shortUrl.asJson.noSpaces).as("application/json")
        }
        else {
          Conflict("There was a problem encoding the URL")
        }
      }
      else
        BadRequest("Invalid application/json request body. Reason: "+eitherErrorOrData.left.get.toString)
    }
    else
      BadRequest("Expecting application/json request body")
  }

  def redirect(short_url: String) = Action {
    val originalUrl = resourceHandler.lookup(short_url)

    if (originalUrl.isEmpty)
      NotFound
    else
      MovedPermanently(originalUrl.get)
        .withHeaders(CACHE_CONTROL -> "no-cache, no-store, max-age=0, must-revalidate")
  }

}
