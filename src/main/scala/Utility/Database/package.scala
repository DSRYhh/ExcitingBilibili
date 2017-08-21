package Utility

import java.util.Calendar

import Utility.Bilibili.{danmu, flatComment, flatVideoInfo}
import Utility.Database.Tables.{rTraversallog, tComments, tDanmu, tTraversallog, tVideo}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by hyh on 2017/8/18.
  */
package object Database
{


    def InsertComment(comment : flatComment) = {
        DBUtil.db.run{
            tComments += Converter.torComment(comment)
        }
    }

    def InsertComment(comments : List[flatComment]) = {
        DBUtil.db.run{
            tComments ++= comments.map(Converter.torComment)
        }
    }

    def InsertVideo(video : flatVideoInfo) = {
        DBUtil.db.run {
            tVideo += Converter.torVideo(video)
        }
    }

    def maxAvVideo(): Future[Option[Int]] = {
        val query = TableQuery[tVideo]
        val statement = query.map(_.av).max.result

        DBUtil.db.run{
            statement
        }
    }

    def latestTraversal() = {

        DBUtil.db.run{
            tTraversallog.sortBy(_.operatetime.desc).map(_.av).result
        }.map(_.toList).map{
            case Nil => None
            case list => Some(list.head)
        }
    }

    def logTraversal(av : Int) = {
        DBUtil.db.run{
            tTraversallog += rTraversallog(0, av, new java.sql.Timestamp(Calendar.getInstance().getTime.getTime))
        }
    }

    def insertDanmu(danmuList : List[danmu]) = {
        DBUtil.db.run{
            tDanmu ++= danmuList.map(Converter.torDanmu)
        }
    }
}
