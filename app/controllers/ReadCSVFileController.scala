package controllers

import javax.inject.Inject

import com.github.tototoshi.csv._
import java.io._

import play.api._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class ReadCSVFileController @Inject() extends Controller {

  def insert = Action(parse.multipartFormData) { request =>
    request.body.file("csv").map { csv =>  
      
      // val reader = CSVReader.open(new File("c:\\WirelessDiagLog.csv"))
      val reader = CSVReader.open(csv.ref.file)
      println (csv.filename)
      println (csv.contentType)
      println (reader.all())
   
    }
      
    Ok("File uploaded")
  }
  
}