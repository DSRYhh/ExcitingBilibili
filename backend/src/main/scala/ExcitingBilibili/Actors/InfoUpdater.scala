package ExcitingBilibili.Actors

import ExcitingBilibili.Actors.Messages.{HandleComplete, HandleError, InsertVideo, UpdateVideo}
import ExcitingBilibili.Exception.{CommentForbiddenException, ParseWebPageException, VideoNotExistException}
import ExcitingBilibili.Utility.Bilibili.{Comment, Video, danmu}
import ExcitingBilibili.Utility.{Bilibili, Database}
import akka.actor.Actor
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by hyh on 2017/8/19.
  */
class InfoUpdater extends Actor {
  private final val logger = LoggerFactory.getLogger(getClass)

  override def receive: Receive = {
    case InsertVideo(av) =>
      Video.getVideoInfo(av.toString).flatMap {
        case Some(videoInfo) =>
          for {_ <- Future {
            Database.InsertVideo(videoInfo)
          }
               _ <- danmu.getDanmu(videoInfo.cid).map(_.foreach(entry => {
                 Database.insertDanmu(videoInfo.cid.toInt, av, entry)
               }))
               _ <- Comment.getAllComment(av.toString).map(comments => {
                 Database.insertComment(Comment.flatten(comments, av))
               })} yield true
        case None =>
          Future {
            false
          }
      }.onComplete {
        case Success(exists) =>
          if (exists) {
            logger.info(s"${context.self.path}: Insert $av succeed.")
            context.parent ! HandleComplete(av)
          } else {
            logger.info(s"${context.self.path}: Video $av does not exist. Skip.")
            context.parent ! HandleComplete(av)
          }

        case Failure(error) =>
          error match {
            case _: CommentForbiddenException =>
              logger.info(s"${context.self.path}: Comment forbidden in $av")
              context.parent ! HandleComplete(av)
            case _: VideoNotExistException =>
              logger.error(s"${context.self.path}: Comment not found in $av")
              context.parent ! HandleComplete(av)
            case parseFail: ParseWebPageException =>
              logger.error(s"${context.self.path}: Insert $av failure with $error: can't match ${parseFail.pattern} in ${parseFail.text}")
              context.parent ! HandleComplete(av)
            case _ => logger.error(s"${context.self.path}: Insert $av failure with $error")
              context.parent ! HandleError(av, error)
          }
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
            case cid =>
              Future {
                Database.updateCid(av, cid)
                updateComment(maxLv, av)
                danmu.getDanmu(cid).map(_.foreach(entry => {
                  Database.insertDanmu(cid.toInt, av, entry)
                }))
              }
          }
        })
      }).onComplete {
        case Success(_) =>
          context.parent ! HandleComplete(av)
          logger.info(s"${context.self.path}: Update $av complete")
        case Failure(error) =>
          context.parent ! HandleError(av, error)
          error match {
            case parseFail: ParseWebPageException =>
              logger.error(s"${context.self.path}: Update $av failure with $error: can't match ${parseFail.pattern} in ${parseFail.text}")
            case _ => logger.error(s"${context.self.path}: Update $av failure with $error")
          }
      }

    case unknown@_ =>
      logger.warn("Unknown message: " + unknown + "  in " + context.self.path.name + " from " + context.sender().path.name)
  }

  private def updateComment(maxLv: Int, av: Int) = {
    val comments: Future[List[Comment]] =
      if (maxLv != -1)
        Comment.getCommentAfter(av.toString, maxLv)
      else
        Comment.getAllComment(av.toString)
    comments.flatMap(c => {
      Database.insertComment(Comment.flatten(c, av))
    })
  }

}
