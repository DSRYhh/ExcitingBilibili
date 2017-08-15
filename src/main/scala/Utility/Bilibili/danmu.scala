package Utility.Bilibili

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.coding.{Deflate, Gzip, NoCoding}
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{HttpEncodingRange, HttpEncodings}
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.{Failure, Success}


/**
  * Created by hyh on 2017/8/15.
  */
object danmu
{
    val baseUri = Uri("http://comment.bilibili.com") //example: http://comment.bilibili.com/21698533.xml

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    def getDanmu(cid : String) = {
        val requestUri = Uri(baseUri.toString() + s"/$cid.xml")

        import akka.http.scaladsl.model.headers
        val user_agent = headers.`User-Agent`("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.90 Safari/537.36")
        val accept_encoding = headers.`Accept-Encoding`(HttpEncodingRange(HttpEncodings.deflate))
        val accept = headers.Accept(MediaRange(MediaTypes.`text/xml`))

        val responseFuture: Future[HttpResponse] =
            Http(system).singleRequest(HttpRequest(GET, uri = requestUri, headers = List(user_agent, accept_encoding, accept)))

        responseFuture
            .map(decodeResponse)
            .map(_.entity)
            .flatMap(_.toStrict(1 seconds)(materializer)) // TODO set timeout
            .map(_.data)
            .map(_.utf8String)
            .map((xmlString : String) =>
                {
                    println()
                }
            )
    }

    def decodeResponse(response: HttpResponse): HttpResponse = {
        val decoder = response.encoding match {
            case HttpEncodings.gzip =>
                Gzip
            case HttpEncodings.deflate =>
                Deflate
            case HttpEncodings.identity =>
                NoCoding
            case _ => throw new IllegalArgumentException("Invalid HTTP encoding")
        }

        decoder.decodeMessage(response)
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
