package Utility.Bilibili

import java.util.zip.{Inflater, InflaterInputStream}

import Utility.{AppSettings, Concurrent}
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
import scala.util.{Failure, Success}


/**
  * Created by hyh on 2017/8/15.
  */
case class danmu(content : String, cid : Int, av : Int)


object danmu
{
    val baseUri = Uri("http://comment.bilibili.com") //example: http://comment.bilibili.com/21698533.xml

    private implicit val system : ActorSystem = Concurrent.system
    private implicit val materializer : ActorMaterializer = Concurrent.materializer

    def getDanmu(cid : String) : Future[List[String]] = {
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
            .map(Source.fromInputStream(_).mkString)
            .map(xmlString => {
                (xml.XML.loadString(xmlString) \ "d").map(_.text).toList
            })
    }
}
