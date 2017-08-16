package Utility

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
}
