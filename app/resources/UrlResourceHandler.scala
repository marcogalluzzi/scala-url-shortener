package resources

import javax.inject.Inject
import repository.UrlRepository
import utils._

class UrlResourceHandler @Inject()(repository: UrlRepository) {

  def createShortURL(resource: UrlResource): Option[UrlResource] = {
    repository.getNextId match {
      case None => None
      case Some(id) => {
        val encodedId = Utils.idEnconder.encode(id)
        val urlResource = UrlResource(resource.originalUrl,
                                      Option(Config.SHORT_URL_DOMAIN + encodedId),
                                      Option(Utils.getCurrentUTCTimeString))
        if (repository.setURL(id, urlResource))
          Option(urlResource)
        else
          None
      }
    }
  }

  def lookup(shortURL: String): Option[String] = {
    Utils.idEnconder.decode(shortURL) match {
      case Nil => None
      case id :: _ => repository.getURL(id) match {
        case None => None
        case Some(data) => {
          repository.incrementCounter(id)
          Option(data.originalUrl)
        }
      }
    }
  }
}
