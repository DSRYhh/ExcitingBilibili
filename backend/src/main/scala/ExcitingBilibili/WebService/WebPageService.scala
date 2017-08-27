package ExcitingBilibili.WebService

import akka.http.scaladsl.server.Directives.{get, getFromResource, path}
import akka.http.scaladsl.server.Route

/**
  * Created by hyh on 2017/8/27.
  */
trait WebPageService {
  val webPageRouter: Route = path("index") {
    get {
      getFromResource("html/index.html")
    }
  }
}
