package utils

import com.typesafe.config.ConfigFactory

object Config {
  private val conf: com.typesafe.config.Config = ConfigFactory.load

  val ENCODER_SALT: String = conf.getString("shortener.encoder-salt")
  val SHORT_URL_DOMAIN = conf.getString("shortener.short-url-domain")
  val REDIS_HOST = conf.getString("shortener.redis.host")
  val REDIS_PORT = conf.getInt("shortener.redis.port")
}
