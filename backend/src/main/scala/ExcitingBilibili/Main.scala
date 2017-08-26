package ExcitingBilibili

import ExcitingBilibili.Actors.Manager
import ExcitingBilibili.Http.ApiRouter
import ExcitingBilibili.Utility.Concurrent
import org.slf4j.LoggerFactory

import scala.io.StdIn

/**
  * The entry of the program
  */
object Main {
  private final val logger = LoggerFactory.getLogger(getClass)

  private implicit val system = Concurrent.system
  private implicit val materializer = Concurrent.materializer

  def main(args: Array[String]): Unit = {
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route = ApiRouter.route

    akka.http.scaladsl.Http().bindAndHandle(route, "localhost", 8080)

    Manager.start()
  }
}
