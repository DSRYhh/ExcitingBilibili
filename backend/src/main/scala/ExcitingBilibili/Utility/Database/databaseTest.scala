package ExcitingBilibili.Utility.Database

import scala.concurrent.ExecutionContext.Implicits.global
import ExcitingBilibili.Utility.Concurrent

import scala.util.{Failure, Success}
/**
  * Created by hyh on 2017/8/24.
  */
object databaseTest {
  val system = Concurrent.system
  val materializer = Concurrent.materializer
  def main(args: Array[String]): Unit = {
    latestVideo(50).onComplete{
      case Success(x) => println(x)
      case Failure(error) => println(error)
    }
  }
}
