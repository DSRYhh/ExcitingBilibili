package ExcitingBilibili.Actors

import akka.actor.Actor
import akka.event.Logging

/**
  * Created by hyh on 2017/8/18.
//  */
class NewListMonitor extends Actor
{
    val log = Logging(context.system, this)

    override def receive: Receive = {
        throw new NotImplementedError
    }
}
