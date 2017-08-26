package ExcitingBilibili.Http

import java.time.LocalDate
import java.time.format.DateTimeParseException

import ExcitingBilibili.Utility.{AppSettings, Database}
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by hyh on 2017/8/24.
  */

object ApiRouter {
  val route: Route =
    get {
      pathSingleSlash {
        redirect("/index", StatusCodes.PermanentRedirect)
      } ~
        pathPrefix("api") {
          path("status") {
              get {
                println(System.currentTimeMillis())
                onComplete(Database.systemStatus().map(_.toString))
                {
                  case Success(json) =>
                    println(System.currentTimeMillis())
                    complete(HttpEntity(ContentTypes.`application/json`, json))
                  case Failure(error) =>
                    complete(HttpResponse(StatusCodes.InternalServerError, entity = s"$error"))
                }
              }
            } ~
          path("data"){
                get {
                  parameters('count ? AppSettings.DefaultQueryDataCount, 'date ? LocalDate.now().toString) { (c, d) =>
                  {
                    onComplete(for {
                      count <- Future{c}
                      date <- Future{LocalDate.parse(d)}
                      l <- Database.latestVideo(count, date)
                    } yield l) {
                      case Success(list) =>
                        import io.circe.syntax._
                        import io.circe.generic.auto._

                        complete(HttpEntity(ContentTypes.`application/json`, list.asJson.noSpaces))
                      case Failure(error) => error match {
                        case _ : DateTimeParseException =>
                          complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Illegal date"))
                        case _ : java.lang.NumberFormatException =>
                          complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Illegal count"))
                      }

                    }
                  }
                  }
                }
              }
        }
    }
}


