package ExcitingBilibili.Actors

import ExcitingBilibili.Actors.Messages.{HandleComplete, HandleError, InsertVideo, UpdateVideo}
import ExcitingBilibili.Exception.{CommentForbiddenException, ParseWebPageException, VideoNotExistException}
import ExcitingBilibili.Utility.Bilibili.{Comment, Video, danmu}
import ExcitingBilibili.Utility.Concurrent.executor
import ExcitingBilibili.Utility.{Bilibili, Database}
import akka.actor.Actor
import org.slf4j.LoggerFactory

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
          logger.debug(s"$av: Get video info succeed.")
          val inserting: Future[Boolean] = {
            val insertVideo = Database.InsertVideo(videoInfo)
              .map(_ => logger.debug(s"$av: Insert video info succeed."))
            val getDanmuAndInsert = danmu.getDanmu(videoInfo.cid, av)
              .map(Database.insertDanmu)
              .map(_ => logger.debug(s"$av: Insert danmu info succeed."))
            val getCommentsAndInsert = Comment.getAllComment(av.toString).map(comments => {
              Database.insertComment(Comment.flatten(comments, av))
            }).map(_ => logger.debug(s"$av: Insert video info succeed."))

            for {
              _ <- insertVideo
              _ <- getDanmuAndInsert
              _ <- getCommentsAndInsert
            } yield true
          }
          inserting
        //          for {_ <- Database.InsertVideo(videoInfo)
        //               _ <- danmu.getDanmu(videoInfo.cid)
        //                 .map(content => content.map((videoInfo.cid.toInt, av, _)))
        //                 .map(Database.insertDanmu)
        //               _ <- Comment.getAllComment(av.toString).map(comments => {
        //                 Database.insertComment(Comment.flatten(comments, av))
        //               })} yield true

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
            case _ =>
              logger.error(s"${context.self.path}: Insert $av failure with unknown error: $error")
              context.parent ! HandleError(av, error)
          }
      }


    case UpdateVideo(av) =>
      Database.getLatestComment(av).flatMap(maxLvOption => {
        val maxLv = maxLvOption match {
          case Some(ml) => ml
          case None => -1
        }
        Video.getBasicInfo(av.toString).flatMap(basicInfo => {
          basicInfo.cid match {
            case Bilibili.NullCidSymbol =>
              updateComment(maxLv, av)
            case cid =>
              Future {
                Database.updateCid(av, cid)
                updateComment(maxLv, av)
                danmu.getDanmu(cid, av)
                  .map(Database.insertDanmu)
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
