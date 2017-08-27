package ExcitingBilibili

import ExcitingBilibili.Actors.Manager
import ExcitingBilibili.WebService.HttpService
import ExcitingBilibili.Utility.Concurrent
import ExcitingBilibili.Utility.Concurrent.executor
import org.slf4j.LoggerFactory

/**
  * The entry of the program
  */
object Boot extends HttpService{

  private implicit val system = Concurrent.system
  private implicit val materializer = Concurrent.materializer

  def main(args: Array[String]): Unit = {
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher

    val route = routes

    akka.http.scaladsl.Http().bindAndHandle(route, "localhost", 8080)

    Manager.start()
  }
}
