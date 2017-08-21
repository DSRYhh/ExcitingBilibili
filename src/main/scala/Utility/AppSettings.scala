package Utility

import com.typesafe.config.{Config, ConfigFactory}

import scala.io.Source

/**
  * Created by hyh on 2017/8/18.
  */
object AppSettings
{
    private val config =  ConfigFactory.load()

    //Slick parameters
    val slickConfig: Config =config.getConfig("slick.db")
    val slickUrl: String = slickConfig.getString("url")
    val slickUser: String = slickConfig.getString("user")
    private val passwordFile = Source.fromFile("src/main/scala/Utility/Database/password")
    val slickPassword: String = try passwordFile.mkString finally passwordFile.close()
//    val slickPassword: String = slickConfig.getString("password")
    val slickMaximumPoolSize: Int = slickConfig.getInt("maximumPoolSize")
    val slickConnectTimeout: Int = slickConfig.getInt("connectTimeout")
    val slickIdleTimeout: Int = slickConfig.getInt("idleTimeout")
    val slickMaxLifetime: Int = slickConfig.getInt("maxLifetime")

    val updateStrategy : Config = config.getConfig("bilibili").getConfig("update_strategy")
    val commentTrackingCount : Int = updateStrategy.getInt("comment_keep_update_count")

    val httpConfig : Config = config.getConfig("bilibili").getConfig("http")
    val StrictWaitingTime: Int = httpConfig.getInt("strict_waiting_time")
}
