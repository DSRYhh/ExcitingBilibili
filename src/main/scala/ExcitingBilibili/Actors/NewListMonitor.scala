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

    private var maxAv : Int = -1

    override def receive: Receive = {
        case InitialLaunch =>
            updateNewList().onComplete{
                case Success(_) =>
                case Failure(error) => logger.error(s"Get new list failed with $error")
            }
        case HandleComplete(av) =>
            if (av == maxAv)
            {
                updateNewList().onComplete{
                    case Success(_) =>
                    case Failure(error) => logger.error(s"Get new list failed with $error")
                }
            }
        case HandleError(av) =>
            if (av == maxAv)
            {
                updateNewList().onComplete{
                    case Success(_) =>
                    case Failure(error) => logger.error(s"Get new list failed with $error")
                }
            }
        case unknown @ _ =>
            logger.warn("Unknown message: " + unknown + "  in " + context.self.path.name + " from " + context.sender().path.name)
    }

    private def updateNewList() = {
        Newlist.updateNewList()
            .map(_._1)
            .map(avList =>
                {
                    maxAv = avList.last
                    avList.foreach(av => {
                        context.child(s"VideoHandler").getOrElse
                        {
                            context.actorOf(Props[VideoHandler](new VideoHandler), s"VideoHandler")
                        } ! HandleVideo(av)
                    })
                }

            )
    }
}
