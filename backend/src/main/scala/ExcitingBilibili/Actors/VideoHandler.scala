package ExcitingBilibili.Actors

import ExcitingBilibili.Actors.Messages._
import ExcitingBilibili.Utility.Database
import akka.actor.{Actor, Props}
import org.slf4j.LoggerFactory

import ExcitingBilibili.Utility.Concurrent.executor
/**
  * Created by hyh on 2017/8/19.
  */

/**
  * Query the database to find if the video has existed or not
  */
class VideoHandler extends Actor {
  private final val logger = LoggerFactory.getLogger(getClass)

  override def receive: Receive = {
    case HandleVideo(av) =>
      Database.containsVideo(av).map {
        case true =>
          updateInfo(UpdateVideo(av))
        case false =>
          updateInfo(InsertVideo(av))
      }
    case HandleComplete(av) =>
      context.parent ! HandleComplete(av)
    case HandleError(av, error) =>
      context.parent ! HandleError(av, error)
    case unknown@_ =>
      logger.warn("Unknown message: " + unknown + "  in " + context.self.path.name + " from " + context.sender().path.name)
  }

  private def updateInfo(message: ActorMessages) = {
    context.child(s"InfoUpdater").getOrElse {
      context.actorOf(Props[InfoUpdater](new InfoUpdater), s"InfoUpdater")
    } ! message
  }
}
