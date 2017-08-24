package ExcitingBilibili

import ExcitingBilibili.Actors.Manager
import org.slf4j.LoggerFactory

/**
  * The entry of the program
  */
object Main {
  private final val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    Manager.start()
  }
}
