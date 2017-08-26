package ExcitingBilibili.Utility

import java.sql.Timestamp
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Calendar

import ExcitingBilibili.Utility.Bilibili.HttpFormat.{BasicVideoInfo, SystemStatus}
import ExcitingBilibili.Utility.Bilibili.{danmu, flatComment, flatVideoInfo}
import ExcitingBilibili.Utility.Database.Tables.{rDanmu, rTraversallog, tComments, tDanmu, tTraversallog, tVideo}
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Success, Try}

/**
  * Created by hyh on 2017/8/18.
  */
package object Database {


  @deprecated("Insert a list is recommended")
  def insertComment(comment: flatComment): Future[Int] = {
    DBUtil.db.run {
      tComments += Converter.torComment(comment)
    }
  }

  def insertComment(comments: List[flatComment]): Future[Any] = {
    comments match {
      case Nil => Future {}
      case _ =>
        DBUtil.db.run {
//          tComments ++= comments.map(Converter.torComment)
          DBIO.sequence(comments.map(c => {
            (tComments += Converter.torComment(c)).asTry
          }))
        }
    }

  }

  def updateCid(av: Int, cid: String): Future[Int] = {
    val cidList = for {entry <- tVideo if entry.av === av} yield entry.cid
    val statement = cidList.update(cid)

    DBUtil.db.run {
      statement
    }

  }

  def getLatestVideos(count: Int): Future[Seq[Int]] = {
    DBUtil.db.run {
      tVideo.sortBy(_.av.desc).take(count).map(_.av).result
    }
  }

  def getLatestComment(av: Int): Future[Option[Int]] = {
    DBUtil.db.run {
      tComments.filter(_.av === av).map(_.lv).max.result
    }
  }

  def InsertVideo(video: flatVideoInfo): Future[Int] = {
    DBUtil.db.run {
      tVideo += Converter.torVideo(video)
    }
  }

  def maxAvVideo(): Future[Option[Int]] = {
    val query = TableQuery[tVideo]
    val statement = query.map(_.av).max.result

    DBUtil.db.run {
      statement
    }
  }

  def containsVideo(av: Int): Future[Boolean] = {
    DBUtil.db.run {
      tVideo.filter(_.av === av).result
    }.map(_.toList.nonEmpty)
  }

  def latestTraversal(): Future[Option[Int]] = {

    DBUtil.db.run {
      tTraversallog.sortBy(_.operatetime.desc).map(_.av).result
    }.map(_.toList).map {
      case Nil => None
      case list => Some(list.head)
    }
  }

  def logTraversal(av: Int): Future[Int] = {
    DBUtil.db.run {
      tTraversallog += rTraversallog(0, av, new java.sql.Timestamp(Calendar.getInstance().getTime.getTime))
    }
  }

  /**
    * Insert a list of danmu
    * @param list a 3-tuple: (cid, av, content)
    */
  def insertDanmu(list : List[(Int, Int, String)]): Future[List[Int]] = {
    val statements = list.map(danmu => {
      val cid = danmu._1
      val av = danmu._2
      val content = danmu._3

      val danmuQuery = TableQuery[tDanmu]
      danmuQuery.forceInsertQuery {
        val exists = (for (entry <- danmuQuery if entry.av === av.bind && entry.danmu === content.bind) yield entry).exists
        val inserttime = new java.sql.Timestamp(Calendar.getInstance().getTime.getTime)
        val insert = (cid.bind,
          av.bind,
          content.bind,
          inserttime.bind) <> (rDanmu.apply _ tupled, rDanmu.unapply)
        for (entry <- Query(insert) if !exists) yield entry
      }
    })

    DBUtil.db.run{
      DBIO.sequence(statements)
    }
  }

  @deprecated("Extremely low performance. Insert the list together")
  def insertDanmu(cid: Int, av: Int, danmu: String): Future[Int] = {
    val danmuQuery = TableQuery[tDanmu]
    val statement = danmuQuery.forceInsertQuery {
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

  def latestVideo(count : Int, date : LocalDate = LocalDate.now()): Future[List[BasicVideoInfo]] = {
    val today = Timestamp.valueOf(date.atStartOfDay())
    val tomorrow = Timestamp.valueOf(date.plus(1, ChronoUnit.DAYS).atStartOfDay())

    DBUtil.db.run{
      tVideo.filter(entry => entry.createtime >= today && entry.createtime <= tomorrow)
        .sortBy(_.createtime.desc)
        .take(count)
        .map(video => {
          (video.av, video.title)
        }).result
    }.map(_.toList.map(entry => {
      BasicVideoInfo(entry._1, entry._2)
    }))
  }

  def systemStatus(): Future[SystemStatus] = {
    val today = LocalDate.now()
    val yesterday = LocalDate.now().minusDays(1)

    DBUtil.db.run {
      for {
        todayDanmu <- danmuCount(today)
        yesterdayDanmu <- danmuCount(yesterday)
        todayVideo <- videoCount(today)
        yesterdayVideo <- videoCount(yesterday)
        todayComment <- commentCount(today)
        yesterdayComment <- commentCount(yesterday)
      } yield SystemStatus(todayVideoCount = todayVideo,
        yesterdayVideoCount = yesterdayVideo,
        todayCommentCount = todayComment,
        yesterdayCommentCount = yesterdayComment,
        todayDanmuCount = todayDanmu,
        yesterdayDanmuCount = yesterdayDanmu)
    }
  }

  private def danmuCount(date: LocalDate) = {
    val today = Timestamp.valueOf(date.atStartOfDay())
    val tomorrow = Timestamp.valueOf(date.plus(1, ChronoUnit.DAYS).atStartOfDay())
    tDanmu.filter(entry =>
      entry.inserttime >= today && entry.inserttime <= tomorrow).length.result
  }

  private def videoCount(date: LocalDate) = {
    val today = Timestamp.valueOf(date.atStartOfDay())
    val tomorrow = Timestamp.valueOf(date.plus(1, ChronoUnit.DAYS).atStartOfDay())
    tVideo.filter(entry =>
      entry.inserttime >= today && entry.inserttime <= tomorrow).length.result
  }

  private def commentCount(date: LocalDate) = {
    val today = Timestamp.valueOf(date.atStartOfDay())
    val tomorrow = Timestamp.valueOf(date.plus(1, ChronoUnit.DAYS).atStartOfDay())
    tComments.filter(entry =>
      entry.inserttime >= today && entry.inserttime <= tomorrow).length.result
  }

}
