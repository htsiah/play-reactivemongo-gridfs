package models

import scala.util.Try

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.Logger
import play.modules.reactivemongo.json._

import reactivemongo.api._
import reactivemongo.api.gridfs.GridFS

object FileModel {
  
  private val dbname = "play-reactivemongo-gridfs-new"
  private val uri = "mongodb://localhost"
  private val driver = new MongoDriver()
  private val connection: Try[MongoConnection] = MongoConnection.parseURI(uri).map { 
    parsedUri => driver.connection(parsedUri)
  }
  private val db = connection.get.db(dbname)
  val gridFS = this.getGridFS

  // let's build an index on our gridfs chunks collection if none
  gridFS.ensureIndex().onComplete {
    case index =>
      Logger.info(s"Checked index, result is $index")
  }
  
  private def getGridFS = {
    import play.modules.reactivemongo.json.collection._
    GridFS[JSONSerializationPack.type](db)
  }

}