package ExcitingBilibili.Utility

import com.typesafe.config.{Config, ConfigFactory}

import scala.io.Source

/**
  * Created by hyh on 2017/8/18.
  */
object AppSettings {
  private val config = ConfigFactory.load()
  private val passwordFile = Source.fromFile("src/main/scala/ExcitingBilibili/Utility/Database/password")

  //Slick parameters
  val slickConfig: Config = config.getConfig("slick.db")
  val slickUrl: String = slickConfig.getString("url")
  val slickUser: String = slickConfig.getString("user")
  val slickPassword: String = try passwordFile.mkString finally passwordFile.close()
  val slickMaximumPoolSize: Int = slickConfig.getInt("maximumPoolSize")
  val slickConnectTimeout: Int = slickConfig.getInt("connectTimeout")
  val slickIdleTimeout: Int = slickConfig.getInt("idleTimeout")
  val slickMaxLifetime: Int = slickConfig.getInt("maxLifetime")

  val updateStrategy: Config = config.getConfig("bilibili").getConfig("update_strategy")
  val commentTrackingCount: Int = updateStrategy.getInt("comment_keep_update_count")
  val defaultMaxAv: Int = updateStrategy.getInt("default_max_av")

  val httpConfig: Config = config.getConfig("bilibili").getConfig("http")
  val StrictWaitingTime: Int = httpConfig.getInt("strict_waiting_time")
  val MaxCommentHandler: Int = httpConfig.getInt("max_comment_handler")
  val MaxMonitorHandler: Int = httpConfig.getInt("max_monitor_handler")
  val MaxTraversalHandler: Int = httpConfig.getInt("max_traversal_handler")
  val MaxRetry: Int = httpConfig.getInt("max_retry")
  val commentPageSize: Int = 10

}
