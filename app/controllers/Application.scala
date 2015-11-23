package controllers

import models.FileModel

import javax.inject.Inject

import play.api._
import play.api.mvc._
import play.modules.reactivemongo.{
  MongoController, ReactiveMongoApi, ReactiveMongoComponents
}
import play.api.libs.json.{ Json, JsObject, JsString }
import play.modules.reactivemongo.json._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import reactivemongo.api.gridfs.ReadFile
import reactivemongo.bson._

class Application @Inject() extends Controller {

  import MongoController.readFileReads
  type JSONReadFile = ReadFile[JSONSerializationPack.type, JsString]
  private val gridFS = FileModel.gridFS

  def index = Action.async {
    gridFS.find[JsObject, JSONReadFile](Json.obj()).collect[List]().map { files =>
      val filesWithId = files.map { file =>
        file.id -> file
      } 
      filesWithId.foreach(file => {
        println(file._1.value)
        println(file._2.filename)
        println(file._2.length)
      })
      
      Ok(views.html.index("Play Reactivemongo - GridFS", Some(filesWithId)))
      
    }
  }
  
}
