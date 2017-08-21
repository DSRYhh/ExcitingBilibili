package Actors

import java.util.concurrent.{SynchronousQueue, ThreadPoolExecutor, TimeUnit}

import Actors.Messages.InitialLaunch
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext

/**
  * Created by hyh on 2017/8/19.
  */
class Manager extends Actor
{
    private final val logger = LoggerFactory.getLogger(getClass)


    override def receive: Receive =
    {
        case InitialLaunch =>
        {
            context.child("NewListMonitor").getOrElse
            {
                context.actorOf(Props[NewListMonitor](new NewListMonitor), "NewListMonitor")
            } ! InitialLaunch
            context.child("CommentUpdater").getOrElse
            {
                context.actorOf(Props[CommentUpdater](new CommentUpdater), "CommentUpdater")
            } ! InitialLaunch
            context.child("Traversal").getOrElse
            {
                context.actorOf(Props[Traversal](new Traversal), "Traversal")
            } ! InitialLaunch
        }


        case unknown@_ =>
            logger.warn("Unknown message: " + unknown + "  in " + context.self.path.name + " from " + context.sender().path.name)
    }
}

object Manager
{
    private final val logger = LoggerFactory.getLogger(getClass)
    val pool = new ThreadPoolExecutor(0, 4096, 60L, TimeUnit.SECONDS, new SynchronousQueue[Runnable]())

    implicit val executionContext: ExecutionContext = ExecutionContext.fromExecutorService(pool)
    implicit val system = ActorSystem("system")

    val actorManager: ActorRef = system.actorOf(Props[Manager], "ActorManager")

    def start() =
    {
        actorManager ! InitialLaunch

        logger.info(s"System start")
    }
}
