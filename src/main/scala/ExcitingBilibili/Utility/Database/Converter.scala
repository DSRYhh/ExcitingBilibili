package ExcitingBilibili.Utility.Database

import java.sql.Timestamp
import java.util.Calendar

import ExcitingBilibili.Utility.Bilibili.{danmu, flatComment, flatVideoInfo}
import ExcitingBilibili.Utility.Database.Tables.{rComments, rDanmu, rVideo}


/**
  * Created by hyh on 2017/8/20.
  */
object Converter
{
    def torVideo(video : flatVideoInfo): ExcitingBilibili.Utility.Database.Tables.rVideo = {
        rVideo(video.av.toInt,
            video.title,
            video.upName,
            video.upMid,
            video.createTime,
            video.zone,
            video.subZone,
            video.cid,
            video.aid,
            video.view,
            video.danmaku,
            video.reply,
            video.favorite,
            video.coin,
            video.share,
            video.now_rank,
            video.his_rank,
            video.like,
            video.no_reprint,
            video.copyright,
            new java.sql.Timestamp(Calendar.getInstance().getTime.getTime))
    }

    def torComment(comment : flatComment): ExcitingBilibili.Utility.Database.Tables.rComments = {
        rComments(comment.av,
            comment.mid,
            comment.lv,
            comment.fbid,
            comment.ad_check,
            comment.good,
            comment.isgood,
            comment.msg,
            comment.device,
            new Timestamp(comment.create),
            comment.create_at,
            comment.reply_count,
            comment.face,
            comment.rank,
            comment.nick,
            comment.current_exp,
            comment.current_level,
            comment.current_min,
            comment.next_exp,
            comment.sex,
            comment.parentFeedBackId,
            new java.sql.Timestamp(Calendar.getInstance().getTime.getTime)
        )
    }

    def torDanmu(dm : danmu): ExcitingBilibili.Utility.Database.Tables.rDanmu = {
        rDanmu(dm.cid, dm.av, dm.content, new java.sql.Timestamp(Calendar.getInstance().getTime.getTime))
    }
}
