package ExcitingBilibili.WebService

import akka.actor.ActorSystem
import akka.event.LoggingAdapter
import akka.http.scaladsl.model.headers.CacheDirectives.{`max-age`, public}
import akka.http.scaladsl.model.headers.`Cache-Control`
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer

import ExcitingBilibili.Utility.Concurrent

/**
  * User: Taoz
  * Date: 11/16/2016
  * Time: 10:37 PM
  *
  * 12/09/2016:   add response compress. by zhangtao
  * 12/09/2016:   add cache support self. by zhangtao
  *
  */
trait ResourceService {

  private val resources = {
    pathPrefix("html") {
      getFromResourceDirectory("html")
    } ~ pathPrefix("css") {
      getFromResourceDirectory("css")
    } ~
      pathPrefix("js") {
        getFromResourceDirectory("js")
      } ~
      pathPrefix("sjsout") {
        getFromResourceDirectory("sjsout")
      } ~
      pathPrefix("img") {
        getFromResourceDirectory("img")
      }
  }

  //cache code copied from zhaorui.
  private val cacheSeconds = 24 * 60 * 60

  val resourceRoutes: Route = (pathPrefix("static") & get) {
    mapResponseHeaders { headers => `Cache-Control`(`public`, `max-age`(cacheSeconds)) +: headers } {
      encodeResponse(resources)
    }
  }


}
