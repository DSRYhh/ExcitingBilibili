package ExcitingBilibili.Actors

import ExcitingBilibili.Actors.Messages.{HandleComplete, HandleError, HandleVideo, InitialLaunch}
import ExcitingBilibili.Utility.{AppSettings, Database}
import akka.actor.{Actor, Props}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

/**
  * Created by hyh on 2017/8/19.
  */
class CommentUpdater extends Actor
{
    private val KEEP_UPDATE_COUNT : Int = AppSettings.commentTrackingCount

    private final val logger = LoggerFactory.getLogger(getClass)
    private val queryQueue = scala.collection.mutable.Queue[Int]()
    override def receive : Unit = {
        case InitialLaunch =>
            updateVideoList()
        case HandleComplete(av) =>
            if (queryQueue.isEmpty)
            {
                updateVideoList()
            }
            else
            {
                updateVideo(queryQueue.dequeue())
            }
        case HandleError(av, error) =>
            if (queryQueue.isEmpty)
            {
                updateVideoList()
            }
            else
            {
                updateVideo(queryQueue.dequeue())
            }
        case unknown @ _ =>
            logger.warn("Unknown message: " + unknown + "  in " + context.self.path.name + " from " + context.sender().path.name)
    }

    private def updateVideoList() = {
        Database.getLatestVideos(KEEP_UPDATE_COUNT).onComplete{
            case Success(list) =>
                list.foreach(queryQueue.enqueue(_))
                if (queryQueue.nonEmpty)
                {
                    updateVideo(queryQueue.dequeue())
                }
                else
                {
                    context.self ! InitialLaunch
                }
            case Failure(error) =>
                logger.error(s"Getting latest video list failure with $error")
        }
    }
    private def updateVideo(av : Int) =
    {
        context.child(s"VideoHandler").getOrElse
        {
            context.actorOf(Props[VideoHandler](new VideoHandler), s"VideoHandler")
        } ! HandleVideo(av)
    }
}
