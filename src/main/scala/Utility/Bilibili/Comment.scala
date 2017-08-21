package Utility.Bilibili

import java.io.{BufferedWriter, File, FileWriter}
import java.util.Date

import Exception.{ParseCommentException, VideoNotExistException}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Source}
import com.typesafe.config.ConfigFactory
import io.circe.generic.auto._
import io.circe.parser.decode
import io.circe.{Decoder, HCursor}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}


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
    def LatestTime: Date =
    {
        list match
        {
            case Some(l) => l.head.time
            case None => throw new NullPointerException
        }
    }

    def OldestTime: Date =
    {
        list match
        {
            case Some(l) => l.last.time
            case None => throw new NullPointerException
        }
    }
}

case class Comment(mid: Int,
                   lv: Int,
                   fbid: String, //feedback id
                   ad_check: Int,
                   good: Int,
                   isgood: Int,
                   msg: String,
                   device: String,
                   create: Long,
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

    override def toString: String = s"$lv -- $nick: $msg\n"

    override def equals(obj: scala.Any): Boolean =
    {
        obj match
        {
            case obj: Comment => this.mid.equals(obj.mid)
            case _ => false
        }
    }
}


case class flatComment(av: Int,
                       mid: Int,
                       lv: Int,
                       fbid: String, //feedback id
                       ad_check: Int,
                       good: Int,
                       isgood: Int,
                       msg: String,
                       device: String,
                       create: Long,
                       create_at: String,
                       reply_count: Int,
                       face: String,
                       rank: Int,
                       nick: String,
                       current_exp: Int,
                       current_level: Int,
                       current_min: Int,
                       next_exp: Int,
                       sex: String,
                       parentFeedBackId: String)

object flatComment
{
    def buildFromComment(comment: Comment, pFbid: String, av: Int): flatComment =
    {
        flatComment(av,
            comment.mid,
            comment.lv,
            comment.fbid,
            comment.ad_check,
            comment.good,
            comment.isgood,
            comment.msg,
            comment.device,
            comment.create,
            comment.create_at,
            comment.reply_count,
            comment.face,
            comment.rank,
            comment.nick,
            comment.level_info.current_exp,
            comment.level_info.current_level,
            comment.level_info.current_min,
            comment.level_info.next_exp,
            comment.sex,
            pFbid)
    }
}

case class UserLevel(current_exp: Int,
                     current_level: Int,
                     current_min: Int,
                     next_exp: Int)

case class ErrorCommentResponse(code: Int,
                                message: String,
                                ts: Int)

object Comment
{
    val baseUri = Uri("http://api.bilibili.cn/feedback")

    private implicit val system = ActorSystem("test", ConfigFactory.load())
    private implicit val materializer = ActorMaterializer()

    private val pool =
        Http().cachedHostConnectionPool[Int]("api.bilibili.cn")


    def getCommentBefore(av: String, date: Date): Future[List[Comment]] =
    {
        throw new NotImplementedError() //TODO implement
    }

    /**
      * Get all comments after the specific date. Return Nil if there is nothing can be returned.
      *
      * @param av   the av number of the video
      * @param date the specific date
      * @return All comments after date
      */
    def getCommentAfter(av: String, date: Date): Future[List[Comment]] =
    {
        getPageCountWithFirstPage(av).map((countAndPage) =>
        {
            val firstPage: CommentPage = countAndPage._2

            var list: List[Comment] = Nil
            if (firstPage.results != 0)
            {
                firstPage.list match
                {
                    case Some(firstList) =>
                        firstList.filter(_.time.after(date)) match
                        {
                            case Nil => Nil
                            case fl =>
                                list = list ::: fl

                                var ifBreak: Boolean = false
                                for (i <- 2 to firstPage.pages if !ifBreak)
                                {
                                    Await.ready(getCommentPage(av, i), Duration.Inf).value.get match
                                    {
                                        case Success(newPage) =>
                                            newPage.list match
                                            {
                                                case Some(newList) =>
                                                    newList.filter(_.time.after(date)) match
                                                    {
                                                        case Nil => ifBreak = true
                                                        case newList: List[Comment] =>
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
        getCommentPage(av).flatMap(firstPage =>
        {
            if (firstPage.results == 0)
            {
                Future
                {
                    Nil
                }
            }
            else if (firstPage.pages == 1)
            {
                Future
                {
                    firstPage.list match
                    {
                        case Some(list) => list
                        case None => Nil
                    }
                }
            }
            else //more than one page
            {
                val list: List[Comment] = firstPage.list match
                {
                    case Some(l) => l
                    case None => Nil
                }

                getCommentPages(av, 2 to firstPage.pages).map(list ::: _)
            }
        })
    }

    @deprecated
    def getAllComment(avList: Range) =
    {
        val requests =
            for (av <- avList)
                yield (commentPageRequestBuilder(av.toString, 1), av)

        Source(requests)
            .via(pool)
            .via(Flow[(Try[HttpResponse], Int)].map(firstPages =>
            {
                firstPages._1 match
                {
                    case Success(response) =>
                        response.entity
                            .toStrict(StrictWaitingTime)
                            .map(_.data.utf8String)
                            .map(parseCommentPage(_, firstPages._2.toString, 1))
                            .map(fp => (fp, firstPages._2))
                    case Failure(error) => throw error
                }
            })).mapAsync(1)(identity)
            .via(Flow[(CommentPage, Int)].map(firstPagePair =>
            {
                val firstPage: CommentPage = firstPagePair._1
                val av: Int = firstPagePair._2
                if (firstPage.results == 0)
                {
                    Future
                    {
                        (Nil, Source.empty)
                    }
                }
                else if (firstPage.pages == 1)
                {
                    Future
                    {
                        firstPage.list match
                        {
                            case Some(list) => (list, Source.empty)
                            case None => (Nil, Source.empty)
                        }
                    }
                }
                else //more than one page
                {
                    val firstList: List[Comment] = firstPage.list match
                    {
                        case Some(l) => l
                        case None => Nil
                    }

                    val requests = for (pageCount <- 2 to firstPage.pages)
                        yield (commentPageRequestBuilder(av.toString, pageCount), pageCount)

                    (firstList, Source(requests))
                }
            }))
    }

    def getLatestComment(page: CommentPage): Option[Comment] =
    {
        page.list match
        {
            case Some(list) => list match
            {
                case Nil => None
                case list: List[Comment] => Some(list.head)
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

    def getPageCountWithFirstPage(av: String): Future[(Int, CommentPage)] =
    {
        getCommentPage(av).map(page => (page.pages, page))
    }

    def getCommentPage(av: String, pageNum: Int = 1): Future[CommentPage] =
    {
        avVerifier(av)

        val queryParameters = Map("aid" -> av, "page" -> pageNum.toString)

        val requestUri: Uri = baseUri.withQuery(Uri.Query(queryParameters))
        val responseFuture: Future[HttpResponse] =
            Http(system).singleRequest(HttpRequest(GET, uri = requestUri))

        responseFuture
            .map(_.entity)
            .flatMap(_.toStrict(StrictWaitingTime)(materializer))
            .map(_.data.utf8String)
            .map(parseCommentPage(_, av, pageNum))
    }

    def getCommentPages(av: String, pages: Range): Future[List[Comment]] =
    {
        avVerifier(av)

        val requests =
            for (pageCount <- pages)
                yield (commentPageRequestBuilder(av, pageCount), pageCount)

        Source(requests)
            .via(pool)
            .via(Flow[(Try[HttpResponse], Int)].map(result =>
            {
                result._1 match
                {
                    case Success(response) =>
                        response.entity
                            .toStrict(StrictWaitingTime)
                            .map(_.data.utf8String)
                            .map(parseCommentPage(_, av, result._2))
                            .map(_.list).map
                        {
                            case Some(l) => l match
                            {
                                case Nil => Nil
                                case list: List[Comment] => list
                            }
                            case None => Nil
                        }
                    case Failure(error) => throw error
                }
            })).mapAsync(1)(identity)
            .runFold(List[Comment]())((oriList, newList) =>
            {
                oriList ::: newList
            })
    }

    def flatten(list: List[Comment], av: Int): List[flatComment] =
    {
        //        list.flatMap(comment => {
        //            comment.reply match {
        //                case Some(l) => l match {
        //                    case Nil => parents ::: List(flatComment.buildFromComment(comment, parentFbid))
        //                    case replayList => flatten(replayList,
        //                        parents ::: List(flatComment.buildFromComment(comment, parentFbid)),
        //                        comment.fbid)
        //                }
        //                case None => parents ::: List(flatComment.buildFromComment(comment, parentFbid))
        //            }
        //        })
        list.map(flatComment.buildFromComment(_, NoneParentFeedBackId, av)) :::
            list.flatMap(comment => comment.reply match
            {
                case Some(l) => l.map(flatComment.buildFromComment(_, comment.fbid, av))
                case None => Nil
            })
    }

    private def commentPageRequestBuilder(av: String, pageNum: Int = 1) =
    {
        avVerifier(av)

        HttpRequest(GET, uri = s"/feedback?aid=$av&page=${pageNum.toString}")
    }


    @throws(classOf[VideoNotExistException])
    @throws(classOf[ParseCommentException])
    private def parseCommentPage(jsonString: String, av: String, page: Int): CommentPage =
    {
        implicit val decodeUserLevel: Decoder[UserLevel] = (c: HCursor) => for
        {
            current_exp <- c.downField("current_exp").as[Int]
            current_level <- c.downField("current_level").as[Int]
            current_min <- c.downField("current_min").as[Int]
            next_exp <- c.downField("next_exp").withFocus(_.mapString
            {
                case """-""" => "-1"
                case default => default
            }).as[Int]
        } yield
                {
                    UserLevel(current_exp, current_level, current_min, next_exp)
                }

        decode[CommentPage](jsonString) match
        {
            case Right(comment) =>
                comment
            case Left(error) =>
                decode[ErrorCommentResponse](jsonString) match
                {
                    case Right(_) => throw VideoNotExistException(av)
                    case Left(_) => throw ParseCommentException(av, page, error)
                }
        }
    }

    def main(args: Array[String]): Unit =
    {


        val fbidSet = new mutable.HashSet[String]()
        (10000 to 11000).toList.foreach(av =>
        {

            Await.ready(getAllComment(av.toString), Duration.Inf)
                .map(c =>
                {
//                    println(c.map(_.reply_count).sum + c.length)
//                    println(c.map(_.lv).diff((1 to c.head.lv).reverse))
                    Comment.flatten(c, av.toInt)
                })
                .onComplete
                {
                    case Success(x) =>
                        x.foreach(c => {
                            if (fbidSet.contains(c.fbid))
                            {
                                println(s"ERROR in ${c.fbid}")
                                val filepath = "D:\\Users Documents\\Desktop\\log.txt"
                                val file = new File(filepath)
                                val bw = new BufferedWriter(new FileWriter(file))
                                bw.write(s"ERROR in ${c.fbid}")
                                bw.close()
                            }
                            else
                            {
                                fbidSet.add(c.fbid)
                                println(s"av : $av complete. Set size : ${fbidSet.size}. fbid : ${c.fbid}")
                            }
                        })
                    case Failure(error) => error match
                    {
                        case nullVideo: VideoNotExistException => //println(s"${nullVideo.av} does not exist.")
                        case unknownError => //println(unknownError)
                    }
                }
        })


    }
}
