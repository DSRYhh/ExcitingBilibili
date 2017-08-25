package ExcitingBilibili.Utility.Bilibili.HttpFormat

import io.circe.generic.auto._
import io.circe.syntax._


/**
  * Created by hyh on 2017/8/23.
  */
case class SystemStatus(todayVideoCount: Int,
                        yesterdayVideoCount: Int,
                        todayCommentCount: Int,
                        yesterdayCommentCount: Int,
                        todayDanmuCount: Int,
                        yesterdayDanmuCount: Int)
{
  override def toString: String = {
    this.asJson.spaces4
  }
}
