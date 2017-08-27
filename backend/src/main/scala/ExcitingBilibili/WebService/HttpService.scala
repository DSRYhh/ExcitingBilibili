package ExcitingBilibili.WebService

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

/**
  * Created by hyh on 2017/8/27.
  */
trait HttpService extends ResourceService
  with WebPageService
  with ApiService {
    val routes: Route = resourceRoutes ~ apiRoute ~ webPageRouter


}
