package ExcitingBilibili.Utility

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by hyh on 2017/8/22.
  */
object Concurrent
{
        implicit val system = ActorSystem("system", ConfigFactory.load())
        implicit val materializer = ActorMaterializer()
}
