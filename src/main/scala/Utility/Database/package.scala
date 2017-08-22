package Utility

import java.util.Calendar

import Utility.Bilibili.{danmu, flatComment, flatVideoInfo}
import Utility.Database.Tables.{rDanmu, rTraversallog, tComments, tDanmu, tTraversallog, tVideo}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

/**
  * Created by hyh on 2017/8/18.
  */
package object Database
{


    def InsertComment(comment : flatComment): Future[Int] = {
        DBUtil.db.run{
            tComments += Converter.torComment(comment)
        }
    }

    def InsertComment(comments : List[flatComment]): Future[Any] = {
        comments match {
            case Nil => Future{}
            case _ =>
                DBUtil.db.run{
                    tComments ++= comments.map(Converter.torComment)
                }
        }

    }

    def updateCid(av : Int, cid : String): Future[Int] = {
        val cids = for {entry <- tVideo if entry.av === av} yield entry.cid
        val statement = cids.update(cid)

        DBUtil.db.run{
            statement
        }

    }

    def getLatestComment(av : Int): Future[Option[Int]] = {
        DBUtil.db.run{
            tComments.filter(_.av === av).map(_.lv).max.result
        }
    }

    def InsertVideo(video : flatVideoInfo): Future[Int] = {
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

    def containsVideo(av : Int): Future[Boolean] = {
        DBUtil.db.run{
            tVideo.filter(_.av === av).result
        }.map(_.toList.nonEmpty)
    }

    def latestTraversal(): Future[Option[Int]] = {

        DBUtil.db.run{
            tTraversallog.sortBy(_.operatetime.desc).map(_.av).result
        }.map(_.toList).map{
            case Nil => None
            case list => Some(list.head)
        }
    }

    def logTraversal(av : Int): Future[Int] = {
        DBUtil.db.run{
            tTraversallog += rTraversallog(0, av, new java.sql.Timestamp(Calendar.getInstance().getTime.getTime))
        }
    }

    @deprecated
    def insertDanmu(danmuList : List[danmu]): Future[Option[Int]] = {
        DBUtil.db.run{
            tDanmu ++= danmuList.map(Converter.torDanmu)
        }
    }

    def insertDanmu(cid : Int, av : Int, danmu : String): Future[Int] = {
        val danmuQuery = TableQuery[tDanmu]
        val statement = danmuQuery.forceInsertQuery{
            val exists = (for (entry <- danmuQuery if entry.av === av.bind && entry.danmu === danmu.bind) yield entry).exists
            val inserttime = new java.sql.Timestamp(Calendar.getInstance().getTime.getTime)
            val insert = (cid.bind,
                av.bind,
                danmu.bind,
                inserttime.bind) <> (rDanmu.apply _ tupled, rDanmu.unapply)
            for (entry <- Query(insert) if !exists) yield entry
        }
        DBUtil.db.run(statement)
    }

}
