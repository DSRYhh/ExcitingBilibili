package Utility.Bilibili

import java.util.zip.{Inflater, InflaterInputStream}

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
import scala.util.{Failure, Success}


/**
  * Created by hyh on 2017/8/15.
  */
object danmu
{
    val baseUri = Uri("http://comment.bilibili.com") //example: http://comment.bilibili.com/21698533.xml

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    def getDanmu(cid : String): Future[String] = {
        val requestUri = Uri(baseUri.toString() + s"/$cid.xml")

        import akka.http.scaladsl.model.headers
        val user_agent = headers.`User-Agent`("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
        val accept_encoding = headers.`Accept-Encoding`(HttpEncodingRange(HttpEncodings.identity))
        val accept = headers.Accept(MediaRange(MediaTypes.`text/xml`))

        val responseFuture: Future[HttpResponse] =
            Http(system).singleRequest(HttpRequest(GET, uri = requestUri, headers = List(user_agent, accept_encoding, accept)))

        responseFuture
            .map(_.entity)
            .flatMap(_.toStrict(1 seconds)(materializer)) // TODO set timeout
            .map(_.dataBytes)
            .map(_.runWith(StreamConverters.asInputStream()))
            .map(new InflaterInputStream(_, new Inflater(true)))
            .map(Source.fromInputStream(_).mkString)

    }


    def main(args: Array[String]): Unit =
    {
        getDanmu("17713827").onComplete
        {
            case Success(x) => print(x)
            case Failure(error)=> println(error)
        }


    }
}
