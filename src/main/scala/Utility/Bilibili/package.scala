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

    val StrictWaitingTime : FiniteDuration = 10 seconds

    val NoneParentFeedBackId : String = "-1"
}
