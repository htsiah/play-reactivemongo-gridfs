package models

import scala.util.Try

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Logger
import play.modules.reactivemongo.json._

import reactivemongo.api._

object FileModel {
  
  private val dbname = "play-reactivemongo-gridfs"
  private val uri = "mongodb://localhost"
  private val driver = new MongoDriver()
  private val connection: Try[MongoConnection] = MongoConnection.parseURI(uri).map { 
    parsedUri => driver.connection(parsedUri)
  }
  private val db = connection.get.db(dbname)

}