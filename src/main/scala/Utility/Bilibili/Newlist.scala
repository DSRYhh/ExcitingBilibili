package Utility.Bilibili

import java.text.SimpleDateFormat
import java.util.Date

import Utility.Bilibili.Video.{materializer, system}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.coding.Gzip
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by hyh on 2017/8/15.
  */
object Newlist
{
    private val baseUri: Uri = Uri("http://www.bilibili.com/newlist.html")


    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    def getNewList(date: Date, typeid: Int, page: Int): Future[(List[Int], Int)] =
    {
        val queryParams = Map("date" -> dateToString(date),
            "typeid" -> typeid.toString,
            "page" -> page.toString)

        getNewList(baseUri.withQuery(Uri.Query(queryParams)))
    }

    def getNewList(date: Date, page: Int): Future[(List[Int], Int)] =
    {
        val queryParams = Map("date" -> dateToString(date),
            "page" -> page.toString)

        getNewList(baseUri.withQuery(Uri.Query(queryParams)))
    }

    def getNewList(typeid: Int, page: Int): Future[(List[Int], Int)] =
    {
        val queryParams = Map("typeid" -> typeid.toString,
            "page" -> page.toString)

        getNewList(baseUri.withQuery(Uri.Query(queryParams)))
    }

    def getNewList(page: Int): Future[(List[Int], Int)] =
    {
        val queryParams = Map("page" -> page.toString)

        getNewList(baseUri.withQuery(Uri.Query(queryParams)))
    }

    /**
      * Get the av number list in the requestUri page
      *
      * @param requestUri the page containing the new video list
      * @return AV number of each video in the list and the end page number
      */
    private def getNewList(requestUri: Uri): Future[(List[Int], Int)] =
    {
        val responseFuture: Future[HttpResponse] =
            Http(system).singleRequest(HttpRequest(GET, uri = requestUri))

        responseFuture
            .map(Gzip.decodeMessage(_))
            .map(_.entity)
            .flatMap(_.toStrict(1 seconds)(materializer)) //TODO network error, and set the timeout
            .map(_.data)
            .map(_.utf8String)
            .map((htmlString: String) =>
            {
                val doc = Jsoup.parse(htmlString)

                import scala.collection.JavaConverters._
                val avList: List[Int] = doc.select("div.new_list")
                    .select("ul.vd_list")
                    .select("li.l1")
                    .asScala
                    .map(parseListElement)
                    .toList

                val endPage: Int = doc.select("a.p.endPage").text().toInt

                (avList, endPage)
            })
    }

    private def parseListElement(li: Element): Int =
    {
        val avLink = li.select("a.preview").attr("href")
        val pattern = s"av([0-9]+)".r
        pattern.findFirstMatchIn(avLink) match
        {
            case Some(m) =>
                val g = m.groupCount
                m.group(1).toInt
            case None => throw new IllegalArgumentException
        }
    }

    private def dateToString(date: Date): String =
    {
        new SimpleDateFormat("yyyy-MM-dd").format(date)
    }

    def main(args: Array[String]): Unit =
    {
        getNewList(baseUri).onComplete
        {
            case Failure(error) => println(error)
            case Success(x) => println(x)
        }
    }
}
