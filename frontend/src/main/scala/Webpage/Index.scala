package Webpage

import io.circe.generic.auto._
import io.circe.parser.decode
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Index {
  def main(args: Array[String]): Unit = {
    appendPar(document.body, "Hello World")
    
//    Ajax.get("api/status").onComplete{
//      case Success(jsonText) =>
//        decode[SystemStatus](jsonText.toString) match {
//          case Right(status) =>
//            addStatusBar(document.body, status)
//          case Left(error) =>
//            println(error)
//        }
//      case Failure(error) =>
//        println(error)
//    }
    
  }

  def addStatusBar(targetNode: dom.Node, status: SystemStatus) = {
    val list = document.createElement("ul")
    list.setAttribute("class", "list-group")

    listItem(list, s"今日评论数：${status.todayCommentCount.toString}")
  }

  private def listItem(targetNode: dom.Node, content : String) = {
    val li = document.createElement("li")
    li.setAttribute("class", "list-group-item")
    val text = document.createTextNode(content)
    li.appendChild(text)
    targetNode.appendChild(li)
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    parNode.setAttribute("class", "alert alert-info")
    parNode.setAttribute("role", "alert")
    val textNode = document.createTextNode(text)
    parNode.appendChild(textNode)
    targetNode.appendChild(parNode)
  }
}