package ExcitingBilibili.Utility

import akka.actor.ActorSystem
import akka.dispatch.MessageDispatcher
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

/**
  * Created by hyh on 2017/8/22.
  */
object Concurrent {
  implicit val system = ActorSystem("system", ConfigFactory.load())
  implicit val materializer = ActorMaterializer()

  implicit val executor: MessageDispatcher =
    system.dispatchers.lookup("akka.actor.my-blocking-dispatcher")
}
