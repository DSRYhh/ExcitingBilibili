package ExcitingBilibili.Actors

import ExcitingBilibili.Actors.Messages.{HandleComplete, HandleError, HandleVideo, InitialLaunch}
import akka.actor.{Actor, Props}
import org.slf4j.LoggerFactory
import ExcitingBilibili.Utility.Bilibili.Newlist

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}
/**
  * Created by hyh on 2017/8/18.
//  */
class NewListMonitor extends Actor
{
    private final val logger = LoggerFactory.getLogger(getClass)

    private val monitorQueue = scala.collection.mutable.Queue[Int]()

    override def receive: Receive = {
        case InitialLaunch =>
            updateQueue().onComplete{
                case Success(_) =>
                case Failure(error) => logger.error(s"Get new list failed with $error")
            }
        case HandleComplete(av) =>
            if (monitorQueue.isEmpty)
            {
                updateQueue().onComplete{
                    case Success(_) =>
                    case Failure(error) => logger.error(s"Get new list failed with $error")
                }
            }
            else
            {
                requestUpdate(monitorQueue.dequeue())
            }
        case HandleError(av, error) =>
            if (monitorQueue.isEmpty)
            {
                updateQueue().onComplete{
                    case Success(_) =>
                    case Failure(listGetError) => logger.error(s"Get new list failed with $listGetError")
                }
            }
            else
            {
                requestUpdate(monitorQueue.dequeue())
            }
        case unknown @ _ =>
            logger.warn("Unknown message: " + unknown + "  in " + context.self.path.name + " from " + context.sender().path.name)
    }

    private def updateQueue() = {
        Newlist.updateNewList()
            .map(_._1)
            .map(avList =>
                {
                    avList.foreach(monitorQueue.enqueue(_))
                    if (monitorQueue.nonEmpty)
                    {
                        requestUpdate(monitorQueue.dequeue())
                    }
                    else
                    {
                        context.self ! InitialLaunch
                    }
                }
            )
    }

    private def requestUpdate(av : Int) = {
        context.child(s"VideoHandler").getOrElse
        {
            context.actorOf(Props[VideoHandler](new VideoHandler), s"VideoHandler")
        } ! HandleVideo(av)
    }
}
