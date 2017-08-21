package Actors

import Actors.Messages.{HandleVideo, InitialLaunch}
import Utility.Database
import akka.actor.{Actor, Props}
/**
  * Created by hyh on 2017/8/19.
  */
class Traversal extends Actor
{
    override def receive: Receive = {
        case InitialLaunch =>
            Database.latestTraversal().map{
                case Some(lastAv) => {
                    context.child(s"HandleVideo$lastAv").getOrElse
                    {
                        context.actorOf(Props[VideoHandler](new VideoHandler), s"HandleVideo$lastAv")
                    } ! HandleVideo(lastAv)
                }
            }

    }
}
