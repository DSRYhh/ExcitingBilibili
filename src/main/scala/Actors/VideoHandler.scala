package Actors

import akka.actor.Actor
import sun.reflect.generics.reflectiveObjects.NotImplementedException

/**
  * Created by hyh on 2017/8/19.
  */

/**
  * Query the database to find if the video has existed or not
  */
class VideoHandler extends Actor
{
    override def receive: Receive = {
        throw new NotImplementedException
    }
}
