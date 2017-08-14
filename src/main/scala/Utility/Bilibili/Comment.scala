package Utility.Bilibili

import java.util.Date

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer
import io.circe.generic.auto._
import io.circe.parser.decode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Success, Failure}


/**
  * Created by hyh on 2017/8/11.
  */

case class CommentPage(results: Int,
                       page: Int,
                       pages: Int,
                       isAdmin: Int,
                       needCode: Boolean,
                       owner: Int,
                       hotList: Option[List[Comment]],
                       list: Option[List[Comment]])
{
    def LatestTime : Date = {
        list match {
            case Some(l) => l.head.time
            case None => throw new NullPointerException
        }
    }

    def OldestTime : Date = {
        list match {
            case Some(l) => l.last.time
            case None => throw new NullPointerException
        }
    }
}

case class Comment(mid: Int,
                   lv: Int,
                   fbid: String,
                   ad_check: Int,
                   good: Int,
                   isgood: Int,
                   msg: String,
                   device: String,
                   create: Int,
                   create_at: String,
                   reply_count: Int,
                   face: String,
                   rank: Int,
                   nick: String,
                   level_info: UserLevel,
                   sex: String,
                   reply: Option[List[Comment]]
                  )
{
    def time: Date =
    {
        new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").parse(create_at)
    }

    override def toString: String = s"$nick: $msg"

    override def equals(obj: scala.Any): Boolean =
    {
        obj match
        {
            case obj: Comment => this.mid.equals(obj.mid)
            case _ => false
        }
    }

}

case class UserLevel(current_exp: Int,
                     current_level: Int,
                     current_min: Int,
                     next_exp: Int)

object Comment
{
    private val baseUri = Uri("http://api.bilibili.cn/feedback")

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    def getCommentBefore(av : String, date: Date) : Future[List[Comment]] = {
        throw new NotImplementedError() //TODO implement
    }

    /**
      * Get all comments after the specific date. Return Nil if there is nothing can be returned.
      * @param av the av number of the video
      * @param date the specific date
      * @return All comments after date
      */
    def getCommentAfter(av : String, date: Date) : Future[List[Comment]] = {
        getPageCountWithFirstPage(av).map((countAndPage) => {
            val firstPage : CommentPage = countAndPage._2

            var list : List[Comment] = Nil
            if (firstPage.results != 0)
            {
                firstPage.list match {
                    case Some(firstList) =>
                        firstList.filter(_.time.after(date)) match {
                            case Nil => Nil
                            case fl =>
                                list = list ::: fl

                                var ifBreak : Boolean = false
                                for (i <- 2 to firstPage.pages if !ifBreak)
                                {
                                    Await.ready(getCommentPage(av, i), Duration.Inf).value.get match {
                                        case Success(newPage) =>
                                            newPage.list match {
                                                case Some(newList) =>
                                                    newList.filter(_.time.after(date)) match {
                                                        case Nil => ifBreak = true
                                                        case newList : List[Comment] =>
                                                            list = list ::: newList
                                                    }
                                                case None => ifBreak = true
                                            }
                                        case Failure(error) => throw error
                                    }
                                }
                                list
                        }
                    case None => Nil
                }
            }
            else
            {
                Nil
            }
        })
    }

    def getAllComment(av: String): Future[List[Comment]] =
    {
        getCommentPage(av).map(firstPage =>
        {
            if (firstPage.results == 0)
            {
                Nil
            }
            else if (firstPage.pages == 1)
            {
                firstPage.list match {
                    case Some(list) => list
                    case None => Nil
                }
            }
            else //more than one page
            {
                var list : List[Comment] = firstPage.list match {
                    case Some(l) => l
                    case None => Nil
                }
                for (pageCount <- 2 to firstPage.pages)
                {
                    Await.ready(getCommentPage(av, pageCount), Duration.Inf).value.get match {
                        case scala.util.Success(page) => page.list match {
                            case Some(newlist) =>
                                list = list ::: newlist
                            case None => Nil
                        }
                        case scala.util.Failure(error) => error
                    }
                }
                list
            }
        })
    }

    def getLatestComment(page: CommentPage): Option[Comment] =
    {
        page.list match
        {
            case Some(list) => list match {
                case Nil => None
                case list : List[Comment] => Some(list.head)
            }
            case None => None
        }
    }

    def getLatestComment(av: String): Future[Option[Comment]] =
    {
        getCommentPage(av).map(getLatestComment)
    }

    def getPageCount(av: String): Future[Int] =
    {
        getPageCountWithFirstPage(av).map(_._1)
    }

    def getPageCountWithFirstPage(av : String) : Future[(Int, CommentPage)] =
    {
        getCommentPage(av).map(page => (page.pages, page))
    }

    def getCommentPage(av: String, pageNum: Int = 1): Future[CommentPage] =
    {
        av.foreach((c) => if (!c.isDigit)
        {
            throw new IllegalArgumentException
        })

        val queryParameters = Map("aid" -> av, "page" -> pageNum.toString)

        val requestUri: Uri = baseUri.withQuery(Uri.Query(queryParameters))
        val responseFuture: Future[HttpResponse] =
            Http(system).singleRequest(HttpRequest(GET, uri = requestUri))
        responseFuture
            .map(_.entity)
            .flatMap(_.toStrict(1 seconds)(materializer)) //TODO network error, and set the timeout
            .map(_.data)
            .map(_.utf8String)
            .map((jsonString: String) =>
            {
                decode[CommentPage](jsonString) match
                {
                    case Right(comment) => comment
                    case Left(error) =>
                        throw error
                }
            })
    }

    def main(args: Array[String]): Unit =
    {
        val av = "10770997"
        getCommentAfter(av, new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").parse("2017-6-1 0:0")).onComplete
        {
            case Success(list) =>
                list.foreach(println(_))
            case Failure(error) => println(error)
        }


    }
}
