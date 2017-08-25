package ExcitingBilibili.Exception

/**
  * Created by hyh on 2017/8/19.
  */
abstract class BilibiliException(av: String) extends Exception

case class VideoNotExistException(av: String) extends BilibiliException(av)
case class CommentForbiddenException(av : String) extends BilibiliException(av)

case class ParseCommentException(av: String, page: Int, error: Throwable) extends BilibiliException(av)

case class ParseWebPageException(av: String, pattern: String, text: String) extends BilibiliException(av)