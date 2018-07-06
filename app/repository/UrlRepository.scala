package repository

import resources.UrlResource

trait UrlRepository {
  def setURL(id: Long, data: UrlResource): Boolean

  def getURL(id: Long): Option[UrlResource]

  def getNextId: Option[Long]

  def incrementCounter(id: Long): Option[Long]
}
