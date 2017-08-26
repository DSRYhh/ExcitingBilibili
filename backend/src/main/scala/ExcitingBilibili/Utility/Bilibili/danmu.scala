package ExcitingBilibili.Utility.Bilibili

import java.sql.Timestamp
import java.util.Calendar
import java.util.zip.{Inflater, InflaterInputStream}

import ExcitingBilibili.Exception.ParseDanmuException
import ExcitingBilibili.Utility.Database.Tables
import ExcitingBilibili.Utility.{AppSettings, Concurrent, Database}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{HttpEncodingRange, HttpEncodings}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.StreamConverters

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.io.Source
import scala.language.postfixOps
import ExcitingBilibili.Utility.Database.Tables.rDanmu

import scala.collection.immutable


/**
  * Created by hyh on 2017/8/15.
  */
case class danmu(content: String, cid: Int, av: Int)


object danmu {
  val baseUri = Uri("http://comment.bilibili.com") //example: http://comment.bilibili.com/21698533.xml

  private implicit val system: ActorSystem = Concurrent.system
  private implicit val materializer: ActorMaterializer = Concurrent.materializer

  @throws(classOf[ParseDanmuException])
  def getDanmu(cid: String, av : Int): Future[List[Database.Tables.rDanmu]] = {
    if (cid.equals(NullCidSymbol)) return Future(Nil)

    val requestUri = Uri(baseUri.toString() + s"/$cid.xml")

    import akka.http.scaladsl.model.headers
    val user_agent = headers.`User-Agent`("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
    val accept_encoding = headers.`Accept-Encoding`(HttpEncodingRange(HttpEncodings.identity))
    val accept = headers.Accept(MediaRange(MediaTypes.`text/xml`))

    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(HttpRequest(GET, uri = requestUri, headers = List(user_agent, accept_encoding, accept)))

    responseFuture
      .map(_.entity)
      .flatMap(_.toStrict(AppSettings.StrictWaitingTime second))
      .map(_.dataBytes)
      .map(_.runWith(StreamConverters.asInputStream()))
      .map(new InflaterInputStream(_, new Inflater(true)))
      .map(Source.fromInputStream(_)("UTF-8").mkString)
      .map(xmlString => {
        (xml.XML.loadString(xmlString) \ "d").map(entry => {
          try {
            val properties = entry.attributes.asAttrMap("p").split(',')
            if (properties.length != 8) throw ParseDanmuException(av.toString, cid.toInt)
            rDanmu(cid = cid.toInt,
              av = av,
              danmu = entry.text,
              sendingtimeinvideo = properties(0).toDouble,
              danmutype = properties(1).toInt,
              fontsize = properties(2).toInt,
              fontcolor = properties(3).toLong,
              sendingtime = new Timestamp(properties(4).toLong * 1000L),
              poolsize = properties(5).toInt,
              senderid = properties(6),
              danmuid = properties(7).toLong,
              inserttime = new java.sql.Timestamp(Calendar.getInstance().getTime.getTime)
            )
          }
          catch {
            case _ : NoSuchElementException => throw ParseDanmuException(av.toString, cid.toInt)
            case error : Throwable => throw error
          }


        })
      }).map(_.toList)
  }
}
