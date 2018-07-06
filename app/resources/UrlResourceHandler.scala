package resources

import javax.inject.Inject
import repository.UrlRepository
import utils._

class UrlResourceHandler @Inject()(repository: UrlRepository) {

  def createShortURL(resource: UrlResource): Option[UrlResource] = {
    val id = repository.getNextId
    if (id.isDefined) {
      val encodedId = Utils.idEnconder.encode(id.get)
      val urlResource = UrlResource(resource.originalUrl, Option(Config.SHORT_URL_DOMAIN + encodedId), Option(Utils.getCurrentUTCTimeString))
      if (repository.setURL(id.get, urlResource))
        return Option(urlResource)
    }
    Option.empty
  }

  def lookup(shortURL: String): Option[String] = {
    val idList = Utils.idEnconder.decode(shortURL)
    if (!idList.isEmpty) {
      val id = idList.head
      val data = repository.getURL(id)
      if (data.isDefined) {
        repository.incrementCounter(id)
        Option(data.get.originalUrl)
      }
      else
        Option.empty
    }
    else
      Option.empty
  }
}
