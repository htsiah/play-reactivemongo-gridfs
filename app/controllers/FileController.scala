package controllers

import models.FileModel

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

  def insert = Action.async(gridFSBodyParser(FileModel.gridFS)) { request => {
    
    // here is the future file!
    val futureFile = request.body.files.head.ref
    
    futureFile.onFailure {
      case err => err.printStackTrace()
    }
          
    // when the upload is complete, we add the some metadata
    val futureUpdate = for {
      file <- futureFile
            
      // here, the file is completely uploaded, so it is time to update the article
      updateResult <- {
        FileModel.gridFS.files.update(
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
    val file = FileModel.gridFS.find[JsObject, JSONReadFile](Json.obj("_id" -> p_id))

    request.getQueryString("inline") match {
      case Some("true") =>
        serve[JsString, JSONReadFile](FileModel.gridFS)(file, CONTENT_DISPOSITION_INLINE)
      case _            => serve[JsString, JSONReadFile](FileModel.gridFS)(file)
    }
    
  }}
  
  def delete(p_id:String) = Action.async {
    FileModel.gridFS.remove(Json toJson p_id).map(_ => 
      Redirect(routes.Application.index)
    ).recover { case _ => InternalServerError }
  }
    
}