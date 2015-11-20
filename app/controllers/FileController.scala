package controllers

import models._

import javax.inject.Inject

import play.api._
import play.api.mvc._
import play.modules.reactivemongo.{
  MongoController, ReactiveMongoApi, ReactiveMongoComponents
}
import play.api.libs.json.{ Json, JsObject, JsString }
import play.modules.reactivemongo.json._, ImplicitBSONHandlers._

import play.api.libs.concurrent.Execution.Implicits.defaultContext

import reactivemongo.api.gridfs.ReadFile
import reactivemongo.bson._

import scala.concurrent.Future

class FileController @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends Controller with MongoController with ReactiveMongoComponents {
    
  import MongoController.readFileReads
  type JSONReadFile = ReadFile[JSONSerializationPack.type, JsString]
  private val gridFS = reactiveMongoApi.gridFS

  // let's build an index on our gridfs chunks collection if none
  gridFS.ensureIndex().onComplete {
    case index =>
      Logger.info(s"Checked index, result is $index")
  }
  
  def insert = Action.async(gridFSBodyParser(gridFS)) { request => {
    
    // here is the future file!
    val futureFile = request.body.files.head.ref
    
    futureFile.onFailure {
      case err => err.printStackTrace()
    }
    
    // when the upload is complete, we add the article id to the file entry (in order to find the attachments of the article)
    val futureUpdate = for {
      file <- { println("_0"); futureFile }
       
      // here, the file is completely uploaded, so it is time to update the article
      updateResult <- {
        println("_1")
        gridFS.files.update(
            Json.obj("_id" -> file.id),
            Json.obj("$set" -> Json.obj("metadata" -> Json.obj("user" -> "Simon Siah", "size" -> "original"))))
        }
      } yield updateResult

      futureUpdate.map { _ =>
        Redirect(routes.Application.index)
      }.recover {
        case e => InternalServerError(e.getMessage())
      }
    
  }}
    
  def view(p_id: String) = Action.async { request => {
    
    // find the matching attachment, if any, and streams it to the client
    val file = gridFS.find[JsObject, JSONReadFile](Json.obj("_id" -> p_id))

    request.getQueryString("inline") match {
      case Some("true") =>
        serve[JsString, JSONReadFile](gridFS)(file, CONTENT_DISPOSITION_INLINE)
      case _            => serve[JsString, JSONReadFile](gridFS)(file)
    }
    
  }}
  
  def delete(p_id:String) = Action.async {
    gridFS.remove(Json toJson p_id).map(_ => 
      Redirect(routes.Application.index)
    ).recover { case _ => InternalServerError }
  }
    
}