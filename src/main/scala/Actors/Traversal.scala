package Actors

import Actors.Messages.{HandleComplete, HandleError, HandleVideo, InitialLaunch}
import Utility.{AppSettings, Database}
import akka.actor.{Actor, Props}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by hyh on 2017/8/19.
  */
class Traversal extends Actor
{
    private final val logger = LoggerFactory.getLogger(getClass)

    private val maxHandler : Int = AppSettings.MaxTraversalHandler
    private var nextHandler: Int = 0

    override def receive: Receive =
    {
        case InitialLaunch =>
            Database.latestTraversal().map
            {
                case Some(lastAv) =>
                    handleVideo(lastAv)
                case None =>
                    handleVideo(1)
            }

        case HandleComplete(av) =>
            Database.maxAvVideo().map(maxAvOption =>
            {
                val maxAv: Int = maxAvOption match
                {
                    case Some(maxAvNo) => maxAvNo
                    case None => AppSettings.defaultMaxAv
                }
                if (av + 1 <= maxAv)
                {
                    handleVideo(av + 1)
                }
                else
                {
                    handleVideo(1)
                }
            })
        case HandleError(av) =>
            handleVideo(av)
        case unknown @ _ =>
            logger.warn("Unknown message: " + unknown + "  in " + context.self.path.name + " from " + context.sender().path.name)

    }

    private def handleVideo(av: Int) =
    {
        context.child(s"VideoHandler$nextHandler").getOrElse
        {
            context.actorOf(Props[VideoHandler](new VideoHandler), s"VideoHandler$nextHandler")
        } ! HandleVideo(av)
        nextHandler = (nextHandler + 1) % maxHandler
    }
}
