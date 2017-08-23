package ExcitingBilibili.Actors

import ExcitingBilibili.Actors.Messages.{HandleComplete, HandleError, HandleVideo, InitialLaunch}
import ExcitingBilibili.Exception.ParseWebPageException
import ExcitingBilibili.Utility.{AppSettings, Database}
import akka.actor.{Actor, Props}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by hyh on 2017/8/19.
  */
class Traversal extends Actor
{
    private final val logger = LoggerFactory.getLogger(getClass)

    private val maxHandler : Int = AppSettings.MaxTraversalHandler
    private var nextHandler: Int = 0

    private var retry : (Int, Int) = (1, 0) //(avNum, retry time)

    override def receive : Receive =
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
            Database.logTraversal(av)
            retry = (av, 0)
            nextAv(av).map(avNum => {
                handleVideo(avNum)
            })
        case HandleError(av, error) =>
            error match {
                case _: ParseWebPageException =>
                    nextAv(av).map(handleVideo)
                case _ =>
                    if (retry._1 == av)
                    {
                        if (retry._2 < AppSettings.MaxRetry)
                        {
                            retry = (av, retry._2 + 1)
                            handleVideo(av)
                        }
                        else
                        {
                            nextAv(av).map(handleVideo)
                            logger.warn(s"Traversal: Retry $av more than ${AppSettings.MaxRetry} times, abort.")
                        }
                    }
                    else
                    {
                        retry = (av, 1)
                        handleVideo(av)
                    }
            }

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

    private def nextAv(av : Int): Future[Int] = {
        if (av != 1)
        {
            Future(av - 1)
        }
        else
        {
            Database.maxAvVideo().map{
                case Some(maxAv) => maxAv
                case None => AppSettings.defaultMaxAv
            }
        }
    }
}
