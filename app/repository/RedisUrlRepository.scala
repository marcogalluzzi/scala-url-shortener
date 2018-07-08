package repository

import javax.inject.Singleton
import com.redis._
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.parser._
import play.api.Logger
import resources.UrlResource
import utils.Config

@Singleton
class RedisUrlRepository extends UrlRepository {

  private val logger = Logger(getClass)

  val URL_KEY_PREFIX = "url:"
  val URL_COUNTER_KEY_PREFIX = "url-counter:"
  val UNIQUE_ID_KEY = "url-id:id"

  val clients = new RedisClientPool(Config.REDIS_HOST, Config.REDIS_PORT)

  def setURL(id: Long, data: UrlResource): Boolean = {
    val key = URL_KEY_PREFIX + id
    val value = data.asJson.noSpaces

    logger.debug(s"Redis setnx key=$key value=$value")

    clients.withClient(_.setnx(key, value))
  }

  def getURL(id: Long): Option[UrlResource] = {
    val key = URL_KEY_PREFIX + id

    logger.debug(s"Redis get key=$key")

    clients.withClient {
      client =>
        client.get[String](key) match {
          case None => None
          case Some(value) => decode[UrlResource](value) match {
            case Left(error) => None
            case Right(resource) => Option(resource)
          }
        }
    }
  }

  def getNextId: Option[Long] = {
    val key = UNIQUE_ID_KEY

    logger.debug(s"Redis incr key=$key")

    clients.withClient(_.incr(key))
  }

  def incrementCounter(id: Long): Option[Long] = {
    val key = URL_COUNTER_KEY_PREFIX + id

    logger.debug(s"Redis incr key=$key")

    clients.withClient(_.incr(key))
  }
}
