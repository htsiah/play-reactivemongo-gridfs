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

class FileAjaxController @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends Controller with MongoController with ReactiveMongoComponents {
  
  import MongoController.readFileReads
  type JSONReadFile = ReadFile[JSONSerializationPack.type, JsString]

  def insert = Action.async(parse.maxLength(1 * 1014 * 1024, gridFSBodyParser(FileModel.gridFS))) { request => {
    
    request.body match {
      case Left(MaxSizeExceeded(length)) => {  
        
        /*
         *  Need to create a schedule agent to remove those file without meta        
        
        FileModel.gridFS.find[JsObject, JSONReadFile](Json.obj("metadata" -> Json.obj("$exists" -> false))).collect[List]().map { files =>
          val filesWithId = files.map { file => {
            println(file.id)
            FileModel.gridFS.remove(Json toJson file.id)
          }}          
        }
        * 
        */
                
        // Return error
        Future(Ok(Json.obj("status" -> "exceed file size limit")).as("application/json"))
      }
      case Right(multipartForm) => {
        // here is the future file!
        val futureFile = request.body.right.get.files.head.ref
        
        // Return error on file upload
        futureFile.onFailure {
          case err => {
            err.printStackTrace();
            Ok(Json.obj("status" -> "error upload file")).as("application/json")
          }
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
        
        // Return success
        futureUpdate.map { _ =>
          Ok(Json.obj("status" -> "success")).as("application/json")
        }.recover {
          case err => {
            err.printStackTrace();
            Ok(Json.obj("status" -> "error update metadata")).as("application/json")
          }
        }
      }
    }
            
  }}
  
  def delete(p_id:String) = Action.async {
    FileModel.gridFS.remove(Json toJson p_id).map(_ => 
      Redirect(routes.Application.fileajax)
    ).recover { case _ => InternalServerError }
  }
    

}