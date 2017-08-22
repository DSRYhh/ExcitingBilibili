package ExcitingBilibili
import ExcitingBilibili.Actors.Manager
import ExcitingBilibili.Utility.AppSettings
import org.slf4j.LoggerFactory

/**
  * The entry of the program
  */
object Main
{
    private final val logger = LoggerFactory.getLogger(getClass)

    def main(args: Array[String]): Unit =
    {
        AppSettings.MaxTraversalHandler
        Manager.start()
    }
}
