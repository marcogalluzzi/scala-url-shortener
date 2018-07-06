package utils

import java.time.{OffsetDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter
import org.pico.hashids.Hashids

object Utils {
  def getCurrentUTCTimeString = {
    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    val utc = OffsetDateTime.now(ZoneOffset.UTC)
    utc.format(format)
  }

  val idEnconder = Hashids.reference(Config.ENCODER_SALT)
}
