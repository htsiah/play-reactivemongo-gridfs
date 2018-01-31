package controllers

import models.FileModel

import javax.inject.Inject

import play.api._
import play.api.mvc._
import play.modules.reactivemongo.{
  MongoController, ReactiveMongoApi, ReactiveMongoComponents
}
import play.api.libs.json.{ Json, JsObject, JsString }
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import reactivemongo.play.json._
import reactivemongo.api.gridfs.ReadFile
import reactivemongo.bson._

class Application @Inject() extends Controller {

  import MongoController.readFileReads
  type JSONReadFile = ReadFile[JSONSerializationPack.type, JsString]

  def index = Action {
      Ok(views.html.index("Play Reactivemongo - GridFS"))
  }
  
  def file = Action.async {
    FileModel.gridFS.find[JsObject, JSONReadFile](Json.obj()).collect[List]().map { files =>
      val filesWithId = files.map { file =>
        file.id -> file
      } 
      
      Ok(views.html.file("Play Reactivemongo - GridFS", Some(filesWithId)))
    }
  }
  
  def fileajax = Action.async {
    FileModel.gridFS.find[JsObject, JSONReadFile](Json.obj()).collect[List]().map { files =>
      val filesWithId = files.map { file =>
        file.id -> file
      } 
      
      Ok(views.html.fileajax("Play Reactivemongo - GridFS", Some(filesWithId)))
    }
  }
  
  def readcsvfile = Action {
    Ok(views.html.readcsvfile("scala-csv - Read CSV File"))
  }
  
}