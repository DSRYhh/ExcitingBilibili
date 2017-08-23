package ExcitingBilibili.Utility.Bilibili

import java.text.SimpleDateFormat
import java.util.Date

import ExcitingBilibili.Utility.{AppSettings, Concurrent}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.coding.Gzip
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{HttpEncodingRange, HttpEncodings}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

/**
  * Created by hyh on 2017/8/15.
  */
object Newlist
{
    private val baseUri: Uri = Uri("http://www.bilibili.com/newlist.html")

    private implicit val system = Concurrent.system
    private implicit val materializer = Concurrent.materializer

    private val pool =
        Http().cachedHostConnectionPool[Int]("www.bilibili.com")

    @deprecated("Can not add any query parameter without logging in")
    private def getNewList(date: Date, typeid: Int, page: Int): Future[(List[Int], Int)] =
    {
        val queryParams = Map("date" -> dateToString(date),
            "typeid" -> typeid.toString,
            "page" -> page.toString)

        getNewList(baseUri.withQuery(Uri.Query(queryParams)))
    }

    @deprecated("Can not add any query parameter without logging in")
    def getNewList(date: Date, page: Int): Future[(List[Int], Int)] =
    {
        val queryParams = Map("date" -> dateToString(date),
            "page" -> page.toString)

        getNewList(baseUri.withQuery(Uri.Query(queryParams)))
    }

    @deprecated("Can not add any query parameter without logging in")
    def getNewList(typeid: Int, page: Int): Future[(List[Int], Int)] =
    {
        val queryParams = Map("typeid" -> typeid.toString,
            "page" -> page.toString)

        getNewList(baseUri.withQuery(Uri.Query(queryParams)))
    }

    @deprecated("Can not add any query parameter without logging in")
    def getNewList(page: Int): Future[(List[Int], Int)] =
    {
        val queryParams = Map("page" -> page.toString)

        getNewList(baseUri.withQuery(Uri.Query(queryParams)))
    }

    /**
      * Get the new video list
      * @return
      */
    def updateNewList() : Future[(List[Int], Int)] = {
        getNewList(baseUri)
    }

    /**
      * Get the av number list in the requestUri page
      *
      * @param requestUri the page containing the new video list
      * @return AV number of each video in the list and the end page number
      */
//    @deprecated("Source based HTTP request is recommended")
    private def getNewList(requestUri: Uri) : Future[(List[Int], Int)] =
    {
        val responseFuture: Future[HttpResponse] =
            Http().singleRequest(HttpRequest(GET, uri = requestUri))

        responseFuture
            .map(Gzip.decodeMessage(_))
            .map(_.entity)
            .flatMap(_.toStrict(AppSettings.StrictWaitingTime second))
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

    @deprecated("Can not add any query parameter without logging in")
    def getNewLists(pages: Range): Future[List[(List[Int], Int)]] =
    {
        import akka.http.scaladsl.model.headers

        val user_agent = headers.`User-Agent`("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
        val accept = headers.`Accept-Encoding`(HttpEncodingRange(HttpEncodings.gzip))

        val requests =
            for (pageCount <- pages)
                yield (HttpRequest(GET, baseUri.withQuery(Uri.Query("page" -> pageCount.toString)), headers = List(accept, user_agent)), pageCount)

        Source(requests)
            .via(pool)
            .via(Flow[(Try[HttpResponse], Int)].map(result =>
            {
                result._1 match
                {
                    case Success(response) =>
                        Gzip.decodeMessage(response)
                            .entity
                            .toStrict(StrictWaitingTime)(materializer)
                            .map(_.data.utf8String)
                            .map(htmlString =>
                            {
                                val doc = Jsoup.parse(htmlString)

                                import scala.collection.JavaConverters._
                                val avList: List[Int] = doc.select("div.new_list")
                                    .select("ul.vd_list")
                                    .select("li.l1")
                                    .asScala
                                    .map(parseListElement)
                                    .toList

                                val test = doc.select("a.p.endPage").text()
                                val endPage: Int = doc.select("a.p.endPage").text().toInt

                                (avList, endPage)
                            })
                    case Failure(error) => throw error
                }
            })).mapAsync(1)(identity).
            runFold(List[(List[Int], Int)]())((oriList, coming) =>
            {
                oriList :+ coming
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

}
