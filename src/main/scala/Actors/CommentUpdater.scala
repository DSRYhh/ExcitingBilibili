package Actors

import Utility.AppSettings
import akka.actor.Actor

/**
  * Created by hyh on 2017/8/19.
  */
class CommentUpdater extends Actor
{
    private val KEEP_UPDATE_COUNT : Int = AppSettings.commentTrackingCount

    override def receive = {
        throw new NotImplementedError()
    }
}
