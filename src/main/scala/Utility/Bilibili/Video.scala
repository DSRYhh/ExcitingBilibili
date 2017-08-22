package Utility.Bilibili
import java.sql.Timestamp
import java.util.Date

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.coding.Gzip
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.headers.{HttpEncoding, HttpEncodingRange}
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer
import io.circe.generic.auto._
import io.circe.parser.decode
import org.jsoup.Jsoup

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.matching.Regex



/**
  * Created by hyh on 2017/8/14.
  */

case class VideoBaiscInfo(av : String,
                          title : String,
                          upName : String,
                          upMid : Int,
                          createTime : Date,
                          zone : String,
                          subZone : String,
                          cid : String)

case class ViewInfoResponse(code : Int,
                            data : VideoViewInfo,
                            message : String,
                            ttl : Int)

case class ErrorViewInfo(code : Int, message : String)

case class VideoViewInfo(aid : Int,
                         view : Int,
                         danmaku : Int,
                         reply : Int,
                         favorite : Int,
                         coin : Int,
                         share : Int,
                         now_rank : Int,
                         his_rank : Int,
                         like : Int,
                         no_reprint : Int,
                         copyright : Int)

case class flatVideoInfo(av : String,
                         title : String,
                         upName : String,
                         upMid : Int,
                         createTime : Timestamp,
                         zone : String,
                         subZone : String,
                         cid : String,
                         aid : Int,
                         view : Int,
                         danmaku : Int,
                         reply : Int,
                         favorite : Int,
                         coin : Int,
                         share : Int,
                         now_rank : Int,
                         his_rank : Int,
                         like : Int,
                         no_reprint : Int,
                         copyright : Int)
object flatVideoInfo {
    def build(viewInfo: VideoViewInfo, basicInfo : VideoBaiscInfo) : flatVideoInfo = {
        flatVideoInfo(basicInfo.av,
            basicInfo.title,
            basicInfo.upName,
            basicInfo.upMid,
            new Timestamp(basicInfo.createTime.getTime),
            basicInfo.zone,
            basicInfo.subZone,
            basicInfo.cid,
            viewInfo.aid,
            viewInfo.view,
            viewInfo.danmaku,
            viewInfo.reply,
            viewInfo.favorite,
            viewInfo.coin,
            viewInfo.share,
            viewInfo.now_rank,
            viewInfo.his_rank,
            viewInfo.like,
            viewInfo.no_reprint,
            viewInfo.copyright)
    }
}

object Video
{

    val baseInfoUri = Uri("http://www.bilibili.com/video")
    val viewInfoUri = Uri("http://api.bilibili.com/archive_stat/stat")

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()


    def getVideoInfo(av : String) : Future[Option[flatVideoInfo]] = {

        getViewInfo(av).flatMap{
            case Right(viewRes) =>
                getBasicInfo(av).map( basicinfo =>
                    Some(flatVideoInfo.build(viewRes.data, basicinfo)))
            case Left(_) =>
                Future(None)
        }
    }

    def getBasicInfo(av : String) : Future[VideoBaiscInfo] = {

        av.foreach((c) => if (!c.isDigit)
        {
            throw new IllegalArgumentException
        })

        val requestUri = Uri(baseInfoUri.toString() + s"/av$av/")

        //construct http request header
        import akka.http.scaladsl.model.headers
        val user_agent = headers.`User-Agent`("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
        val accept_encoding = headers.`Accept-Encoding`(HttpEncodingRange(HttpEncoding("gzip, deflate, br")))

        val responseFuture: Future[HttpResponse] =
            Http(system).singleRequest(HttpRequest(GET, uri = requestUri, headers = List(user_agent, accept_encoding)))

        responseFuture
            .map(Gzip.decodeMessage(_))
            .map(_.entity)
            .flatMap(_.toStrict(StrictWaitingTime)(materializer)) //TODO network error, and set the timeout
            .map(_.data)
            .map(_.utf8String)
            .map((htmlString: String) =>
            {
                val doc = Jsoup.parse(htmlString)

                val upInfoBlock = doc.body().select("div.upinfo").select("div.r-info")
                val upInfo = upInfoBlock.size() match {
                    case 0 => (NullUpName, NullUpMid)
                    case _ => (upInfoBlock.select("a.name").text(), upInfoBlock.select("a.message").attr("mid"))
                }
                val upName = upInfo._1
                val upMid = upInfo._2

                val pageTitle = doc.title()
                val videoTitle = doc.body().select("div.v-title").select("h1").text()
                val videoPattern  = s"${Regex.quote(videoTitle)}_(\\S+?)_(\\S+?)_bilibili_哔哩哔哩".r
                val matchResult = videoPattern.findAllIn(pageTitle)
                val subZone = matchResult.group(1)
                val zone = matchResult.group(2)

                val createTime = doc.body().select("div.main-inner").select("div.tminfo").first().select("time").text() match {
                    case "" => NullCreateTime
                    case time => time
                }

                val pattern : Regex = "cid=([0-9]+?)&".r
                val cidMatchResult = pattern.findFirstMatchIn(doc.body().toString)
                val cid = cidMatchResult match {
                    case Some(mat) => mat.group(1)
                    case None => NullCidSymbol
                }


                VideoBaiscInfo(av,
                    videoTitle,
                    upName,
                    upMid.toInt,
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").parse(createTime),
                    zone,
                    subZone,
                    cid)
            })
    }

    private def getViewInfo(av : String) : Future[Either[ErrorViewInfo ,ViewInfoResponse]] = {
        av.foreach((c) => if (!c.isDigit)
        {
            throw new IllegalArgumentException
        })

        val requestUri = viewInfoUri.withQuery(Uri.Query("aid" -> av))

        val responseFuture: Future[HttpResponse] =
            Http(system).singleRequest(HttpRequest(GET, uri = requestUri))

        responseFuture
            .map(_.entity)
            .flatMap(_.toStrict(StrictWaitingTime)(materializer))
            .map(_.data)
            .map(_.utf8String)
            .map((jsonString : String) =>
            {
                decode[ViewInfoResponse](jsonString) match
                {
                    case Right(response) =>
                        Right(response)
                    case Left(error) =>
                        decode[ErrorViewInfo](jsonString) match {
                            case Right(errorResponse) => Left(errorResponse)
                            case Left(_) => throw error
                        }
                }
            })
    }

    def main(args: Array[String]): Unit =
    {
//        getBasicInfo("6701825").onComplete
//        {
//            case Success(x) => println(x)
//            case Failure(error) => println(error)
//        }
//        getViewInfo("6701825").onComplete
//        {
//            case Success(x) => println(x)
//            case Failure(error) => println(error)
//        }



        getVideoInfo("6701825").map{
            case Some(info) => println(info)
            case None => println("Video not exists")
        }
    }
}
