package Utility.Bilibili
import java.util.Date

import scala.concurrent.ExecutionContext.Implicits.global
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.coding.Gzip
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.headers.{HttpEncoding, HttpEncodingRange}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer
import org.jsoup.Jsoup

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.matching.Regex
import scala.util.{Failure, Success}



/**
  * Created by hyh on 2017/8/14.
  */

case class VideoBaiscInfo(av : String,
                          title : String,
                          upName : String,
                          upMid : Int,
                          createTime : Date,
                          zone : String,
                          subZone : String)

object Video
{

    val baseInfoUri = Uri("http://www.bilibili.com/video")
    val viewInfoUri = Uri("http://api.bilibili.com/archive_stat/stat")

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()


    def getVideoPage(av : String) : Future[VideoBaiscInfo] = {

        av.foreach((c) => if (!c.isDigit)
        {
            throw new IllegalArgumentException
        })

        val requestUri = Uri(baseInfoUri.toString() + s"/av$av/")

        import akka.http.scaladsl.model.headers
        val user_agent = headers.`User-Agent`("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
        val accept_encoding = headers.`Accept-Encoding`(HttpEncodingRange(HttpEncoding("gzip, deflate, br")))

        val responseFuture: Future[HttpResponse] =
            Http(system).singleRequest(HttpRequest(GET, uri = requestUri, headers = List(user_agent, accept_encoding)))

        responseFuture
            .map(Gzip.decodeMessage(_))
            .map(_.entity)
            .flatMap(_.toStrict(1 seconds)(materializer)) //TODO network error, and set the timeout
            .map(_.data)
            .map(_.utf8String)
            .map((htmlString: String) =>
            {
                val doc = Jsoup.parse(htmlString)

                val upInfo = doc.body().select("div.upinfo").select("div.r-info").first()
                val upName = upInfo.select("a.name").text()
                val upMid = upInfo.select("a.message").attr("mid")

                val pageTitle = doc.title()
                val videoTitle = doc.body().select("div.v-title").select("h1").text()
                val pattern  = s"${videoTitle}_(\\S+?)_(\\S+?)_bilibili_哔哩哔哩".r
                val matchResult = pattern.findAllIn(pageTitle)
                val subZone = matchResult.group(1)
                val zone = matchResult.group(2)

                val createTime = doc.body().select("div.main-inner").select("div.tminfo").first().select("time").text()

                VideoBaiscInfo(av,
                    videoTitle,
                    upName,
                    upMid.toInt,
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").parse(createTime),
                    zone,
                    subZone)
            })
    }

    def main(args: Array[String]): Unit =
    {
        getVideoPage("6701825").onComplete
        {
            case Success(x) => println(x)
            case Failure(error) => println(error)
        }
    }
}
