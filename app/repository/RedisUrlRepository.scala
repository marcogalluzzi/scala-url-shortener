package repository

import javax.inject.Singleton
import com.redis._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._
import resources.UrlResource
import utils.Config

@Singleton
class RedisUrlRepository extends UrlRepository {

  val URL_KEY_PREFIX = "url:"
  val URL_COUNTER_KEY_PREFIX = "url-counter:"
  val UNIQUE_ID_KEY = "url-id:id"

  val clients = new RedisClientPool(Config.REDIS_HOST, Config.REDIS_PORT)

  def setURL(id: Long, data: UrlResource): Boolean = {
    val key = URL_KEY_PREFIX + id
    val value = data.asJson.noSpaces

    clients.withClient(_.setnx(key, value))
  }

  def getURL(id: Long): Option[UrlResource] = {
    val key = URL_KEY_PREFIX + id

    clients.withClient {
      client =>
        client.get[String](key) match {
          case Some(value) => decode[UrlResource](value) match {
            case Right(resource) => return Option(resource)
          }
        }
    }
    None
  }

  def getNextId: Option[Long] = {
    val key = UNIQUE_ID_KEY

    clients.withClient(_.incr(key))
  }

  def incrementCounter(id: Long): Option[Long] = {
    val key = URL_COUNTER_KEY_PREFIX + id

    clients.withClient(_.incr(key))
  }
}
