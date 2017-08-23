package ExcitingBilibili
import ExcitingBilibili.Actors.Manager
import ExcitingBilibili.Utility.AppSettings
import org.slf4j.LoggerFactory

import scala.util.matching.Regex

/**
  * The entry of the program
  */
object Main
{
    private final val logger = LoggerFactory.getLogger(getClass)

    def main(args: Array[String]): Unit =
    {
//        val title = "【MHA /切岛锐儿郎】英雄 doa _综合_动画_bilibili_哔哩哔哩"
//        val videoTitle = "【MHA /切岛锐儿郎】英雄 doa"
//        val videoPattern  = s"${Regex.quote(videoTitle)}(\\s+)?_(\\S+?)_(\\S+?)_bilibili_哔哩哔哩".r
//        val matchResult = videoPattern.findAllIn(title)
//        val result = matchResult.group(1)
        Manager.start()
    }
}
