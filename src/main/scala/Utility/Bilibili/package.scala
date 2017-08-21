package Utility


import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by hyh on 2017/8/16.
  */
package object Bilibili
{
    def avVerifier(av : String) : Unit = {
        av.foreach((c) => if (!c.isDigit)
        {
            throw new IllegalArgumentException
        })
    }

    val StrictWaitingTime : FiniteDuration = AppSettings.StrictWaitingTime second

    val NoneParentFeedBackId : String = "-1"

    val NullCidSymbol : String = "-1"

    val NullUpName : String = "null"
    val NullUpMid : String = "-1"

    val NullCreateTime : String = "1970-01-01 00:00"
}
