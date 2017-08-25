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

    val bindingFuture = akka.http.scaladsl.Http().bindAndHandle(route, "localhost", 8080)

//    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
//    StdIn.readLine() // let it run until user presses return
//    bindingFuture
//      .flatMap(_.unbind()) // trigger unbinding from the port
//      .onComplete(_ => system.terminate()) // and shutdown when done

    Manager.start()
  }
}
