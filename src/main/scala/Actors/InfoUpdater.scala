package Actors

import Actors.Messages.{HandleComplete, HandleError, InsertVideo, UpdateVideo}
import Utility.Bilibili.{Comment, Video}
import Utility.{Bilibili, Database}
import akka.actor.Actor
import org.slf4j.LoggerFactory

import scala.concurrent.Future
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by hyh on 2017/8/19.
  */
class InfoUpdater extends Actor
{
    private final val logger = LoggerFactory.getLogger(getClass)

    override def receive: Receive = {
        case InsertVideo(av) =>
            Video.getVideoInfo(av.toString).map{
                case Some(videoInfo) =>
                    Database.InsertVideo(videoInfo)
                    Comment.getAllComment(av.toString).map(comments => {
                        Database.InsertComment(Comment.flatten(comments,av))
                    }).onComplete{
                        case Success(_) =>
                            logger.info(s"Insert $av succeed.")
                            context.parent ! HandleComplete(av)
                        case Failure(error) =>
                            logger.error(s"Error occurred: ${error.toString}")
                            context.parent ! HandleError(av)
                    }
                case None =>
                    logger.info(s"Video $av does not exist. Skip.")
                    context.parent ! HandleComplete(av)
            }

        case UpdateVideo(av) =>
            Database.getLatestComment(av).flatMap(maxLvOption => {
                val maxLv = maxLvOption match {
                    case Some(ml) => ml
                    case None => -1
                }

                Video.getBasicInfo(av.toString).map(basicInfo => {
                    basicInfo.cid match {
                        case Bilibili.NullCidSymbol =>
                            updateComment(maxLv, av)
                            //TODO update danmu
                        case cid =>
                            Database.updateCid(av, cid)
                            updateComment(maxLv, av)
                            //TODO update danmu, combine two futures
                    }
                })
            })

        case unknown @ _ =>
            logger.warn("Unknown message: " + unknown + "  in " + context.self.path.name + " from " + context.sender().path.name)
    }

    private def updateComment(maxLv : Int, av : Int) = {
        val comments : Future[List[Comment]] =
            if (maxLv != -1)
                Comment.getCommentAfter(av.toString, maxLv)
            else
                Comment.getAllComment(av.toString)
        comments.flatMap(c => {
            Database.InsertComment(Comment.flatten(c, av))
        })
    }

    private def updateDanmu(av : Int, cid : Int) = {

    }
}
