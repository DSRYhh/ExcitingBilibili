package ExcitingBilibili.Utility.Database
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.PostgresProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = tComments.schema ++ tDanmu.schema ++ tTraversallog.schema ++ tVideo.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tComments
    *  @param av Database column av SqlType(int4)
    *  @param mid Database column mid SqlType(int4)
    *  @param lv Database column lv SqlType(int4)
    *  @param fbid Database column fbid SqlType(varchar), PrimaryKey, Length(255,true)
    *  @param adCheck Database column ad_check SqlType(int4)
    *  @param good Database column good SqlType(int4)
    *  @param isgood Database column isgood SqlType(int4)
    *  @param msg Database column msg SqlType(text)
    *  @param device Database column device SqlType(varchar), Length(255,true)
    *  @param createunixtime Database column createunixtime SqlType(timestamp)
    *  @param createAt Database column create_at SqlType(varchar), Length(255,true)
    *  @param replyCount Database column reply_count SqlType(int4)
    *  @param face Database column face SqlType(text)
    *  @param rank Database column rank SqlType(int4)
    *  @param nick Database column nick SqlType(varchar), Length(255,true)
    *  @param currentExp Database column current_exp SqlType(int4)
    *  @param currentLevel Database column current_level SqlType(int4)
    *  @param currentMin Database column current_min SqlType(int4)
    *  @param nextExp Database column next_exp SqlType(int4)
    *  @param sex Database column sex SqlType(varchar), Length(255,true)
    *  @param parentfeedbackid Database column parentfeedbackid SqlType(varchar), Length(255,true)
    *  @param inserttime Database column inserttime SqlType(timestamp) */
  final case class rComments(av: Int, mid: Int, lv: Int, fbid: String, adCheck: Int, good: Int, isgood: Int, msg: String, device: String, createunixtime: java.sql.Timestamp, createAt: String, replyCount: Int, face: String, rank: Int, nick: String, currentExp: Int, currentLevel: Int, currentMin: Int, nextExp: Int, sex: String, parentfeedbackid: String, inserttime: java.sql.Timestamp)
  /** GetResult implicit for fetching rComments objects using plain SQL queries */
  implicit def GetResultrComments(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[rComments] = GR{
    prs => import prs._
      rComments.tupled((<<[Int], <<[Int], <<[Int], <<[String], <<[Int], <<[Int], <<[Int], <<[String], <<[String], <<[java.sql.Timestamp], <<[String], <<[Int], <<[String], <<[Int], <<[String], <<[Int], <<[Int], <<[Int], <<[Int], <<[String], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table comments. Objects of this class serve as prototypes for rows in queries. */
  class tComments(_tableTag: Tag) extends profile.api.Table[rComments](_tableTag, "comments") {
    def * = (av, mid, lv, fbid, adCheck, good, isgood, msg, device, createunixtime, createAt, replyCount, face, rank, nick, currentExp, currentLevel, currentMin, nextExp, sex, parentfeedbackid, inserttime) <> (rComments.tupled, rComments.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(av), Rep.Some(mid), Rep.Some(lv), Rep.Some(fbid), Rep.Some(adCheck), Rep.Some(good), Rep.Some(isgood), Rep.Some(msg), Rep.Some(device), Rep.Some(createunixtime), Rep.Some(createAt), Rep.Some(replyCount), Rep.Some(face), Rep.Some(rank), Rep.Some(nick), Rep.Some(currentExp), Rep.Some(currentLevel), Rep.Some(currentMin), Rep.Some(nextExp), Rep.Some(sex), Rep.Some(parentfeedbackid), Rep.Some(inserttime)).shaped.<>({r=>import r._; _1.map(_=> rComments.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get, _19.get, _20.get, _21.get, _22.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column av SqlType(int4) */
    val av: Rep[Int] = column[Int]("av")
    /** Database column mid SqlType(int4) */
    val mid: Rep[Int] = column[Int]("mid")
    /** Database column lv SqlType(int4) */
    val lv: Rep[Int] = column[Int]("lv")
    /** Database column fbid SqlType(varchar), PrimaryKey, Length(255,true) */
    val fbid: Rep[String] = column[String]("fbid", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column ad_check SqlType(int4) */
    val adCheck: Rep[Int] = column[Int]("ad_check")
    /** Database column good SqlType(int4) */
    val good: Rep[Int] = column[Int]("good")
    /** Database column isgood SqlType(int4) */
    val isgood: Rep[Int] = column[Int]("isgood")
    /** Database column msg SqlType(text) */
    val msg: Rep[String] = column[String]("msg")
    /** Database column device SqlType(varchar), Length(255,true) */
    val device: Rep[String] = column[String]("device", O.Length(255,varying=true))
    /** Database column createunixtime SqlType(timestamp) */
    val createunixtime: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("createunixtime")
    /** Database column create_at SqlType(varchar), Length(255,true) */
    val createAt: Rep[String] = column[String]("create_at", O.Length(255,varying=true))
    /** Database column reply_count SqlType(int4) */
    val replyCount: Rep[Int] = column[Int]("reply_count")
    /** Database column face SqlType(text) */
    val face: Rep[String] = column[String]("face")
    /** Database column rank SqlType(int4) */
    val rank: Rep[Int] = column[Int]("rank")
    /** Database column nick SqlType(varchar), Length(255,true) */
    val nick: Rep[String] = column[String]("nick", O.Length(255,varying=true))
    /** Database column current_exp SqlType(int4) */
    val currentExp: Rep[Int] = column[Int]("current_exp")
    /** Database column current_level SqlType(int4) */
    val currentLevel: Rep[Int] = column[Int]("current_level")
    /** Database column current_min SqlType(int4) */
    val currentMin: Rep[Int] = column[Int]("current_min")
    /** Database column next_exp SqlType(int4) */
    val nextExp: Rep[Int] = column[Int]("next_exp")
    /** Database column sex SqlType(varchar), Length(255,true) */
    val sex: Rep[String] = column[String]("sex", O.Length(255,varying=true))
    /** Database column parentfeedbackid SqlType(varchar), Length(255,true) */
    val parentfeedbackid: Rep[String] = column[String]("parentfeedbackid", O.Length(255,varying=true))
    /** Database column inserttime SqlType(timestamp) */
    val inserttime: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("inserttime")
  }
  /** Collection-like TableQuery object for table tComments */
  lazy val tComments = new TableQuery(tag => new tComments(tag))

  /** Entity class storing rows of table tDanmu
    *  @param cid Database column cid SqlType(int4)
    *  @param av Database column av SqlType(int4)
    *  @param danmu Database column danmu SqlType(text)
    *  @param inserttime Database column inserttime SqlType(timestamp) */
  final case class rDanmu(cid: Int, av: Int, danmu: String, inserttime: java.sql.Timestamp)
  /** GetResult implicit for fetching rDanmu objects using plain SQL queries */
  implicit def GetResultrDanmu(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[rDanmu] = GR{
    prs => import prs._
      rDanmu.tupled((<<[Int], <<[Int], <<[String], <<[java.sql.Timestamp]))
  }
  /** Table description of table danmu. Objects of this class serve as prototypes for rows in queries. */
  class tDanmu(_tableTag: Tag) extends profile.api.Table[rDanmu](_tableTag, "danmu") {
    def * = (cid, av, danmu, inserttime) <> (rDanmu.tupled, rDanmu.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(cid), Rep.Some(av), Rep.Some(danmu), Rep.Some(inserttime)).shaped.<>({r=>import r._; _1.map(_=> rDanmu.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column cid SqlType(int4) */
    val cid: Rep[Int] = column[Int]("cid")
    /** Database column av SqlType(int4) */
    val av: Rep[Int] = column[Int]("av")
    /** Database column danmu SqlType(text) */
    val danmu: Rep[String] = column[String]("danmu")
    /** Database column inserttime SqlType(timestamp) */
    val inserttime: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("inserttime")
  }
  /** Collection-like TableQuery object for table tDanmu */
  lazy val tDanmu = new TableQuery(tag => new tDanmu(tag))

  /** Entity class storing rows of table tTraversallog
    *  @param index Database column index SqlType(serial), AutoInc, PrimaryKey
    *  @param av Database column av SqlType(int4)
    *  @param operatetime Database column operatetime SqlType(timestamp) */
  final case class rTraversallog(index: Int, av: Int, operatetime: java.sql.Timestamp)
  /** GetResult implicit for fetching rTraversallog objects using plain SQL queries */
  implicit def GetResultrTraversallog(implicit e0: GR[Int], e1: GR[java.sql.Timestamp]): GR[rTraversallog] = GR{
    prs => import prs._
      rTraversallog.tupled((<<[Int], <<[Int], <<[java.sql.Timestamp]))
  }
  /** Table description of table traversallog. Objects of this class serve as prototypes for rows in queries. */
  class tTraversallog(_tableTag: Tag) extends profile.api.Table[rTraversallog](_tableTag, "traversallog") {
    def * = (index, av, operatetime) <> (rTraversallog.tupled, rTraversallog.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(index), Rep.Some(av), Rep.Some(operatetime)).shaped.<>({r=>import r._; _1.map(_=> rTraversallog.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column index SqlType(serial), AutoInc, PrimaryKey */
    val index: Rep[Int] = column[Int]("index", O.AutoInc, O.PrimaryKey)
    /** Database column av SqlType(int4) */
    val av: Rep[Int] = column[Int]("av")
    /** Database column operatetime SqlType(timestamp) */
    val operatetime: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("operatetime")
  }
  /** Collection-like TableQuery object for table tTraversallog */
  lazy val tTraversallog = new TableQuery(tag => new tTraversallog(tag))

  /** Entity class storing rows of table tVideo
    *  @param av Database column av SqlType(int4), PrimaryKey
    *  @param title Database column title SqlType(text)
    *  @param upname Database column upname SqlType(varchar), Length(1024,true)
    *  @param upmid Database column upmid SqlType(int4)
    *  @param createtime Database column createtime SqlType(timestamp)
    *  @param zone Database column zone SqlType(varchar), Length(255,true)
    *  @param subzone Database column subzone SqlType(varchar), Length(255,true)
    *  @param cid Database column cid SqlType(varchar), Length(255,true)
    *  @param aid Database column aid SqlType(int4)
    *  @param viewcount Database column viewcount SqlType(int4)
    *  @param danmaku Database column danmaku SqlType(int4)
    *  @param reply Database column reply SqlType(int4)
    *  @param favorite Database column favorite SqlType(int4)
    *  @param coin Database column coin SqlType(int4)
    *  @param sharecount Database column sharecount SqlType(int4)
    *  @param nowRank Database column now_rank SqlType(int4)
    *  @param hisRank Database column his_rank SqlType(int4)
    *  @param likecount Database column likecount SqlType(int4)
    *  @param noReprint Database column no_reprint SqlType(int4)
    *  @param copyright Database column copyright SqlType(int4)
    *  @param inserttime Database column inserttime SqlType(timestamp) */
  final case class rVideo(av: Int, title: String, upname: String, upmid: Int, createtime: java.sql.Timestamp, zone: String, subzone: String, cid: String, aid: Int, viewcount: Int, danmaku: Int, reply: Int, favorite: Int, coin: Int, sharecount: Int, nowRank: Int, hisRank: Int, likecount: Int, noReprint: Int, copyright: Int, inserttime: java.sql.Timestamp)
  /** GetResult implicit for fetching rVideo objects using plain SQL queries */
  implicit def GetResultrVideo(implicit e0: GR[Int], e1: GR[String], e2: GR[java.sql.Timestamp]): GR[rVideo] = GR{
    prs => import prs._
      rVideo.tupled((<<[Int], <<[String], <<[String], <<[Int], <<[java.sql.Timestamp], <<[String], <<[String], <<[String], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[java.sql.Timestamp]))
  }
  /** Table description of table video. Objects of this class serve as prototypes for rows in queries. */
  class tVideo(_tableTag: Tag) extends profile.api.Table[rVideo](_tableTag, "video") {
    def * = (av, title, upname, upmid, createtime, zone, subzone, cid, aid, viewcount, danmaku, reply, favorite, coin, sharecount, nowRank, hisRank, likecount, noReprint, copyright, inserttime) <> (rVideo.tupled, rVideo.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(av), Rep.Some(title), Rep.Some(upname), Rep.Some(upmid), Rep.Some(createtime), Rep.Some(zone), Rep.Some(subzone), Rep.Some(cid), Rep.Some(aid), Rep.Some(viewcount), Rep.Some(danmaku), Rep.Some(reply), Rep.Some(favorite), Rep.Some(coin), Rep.Some(sharecount), Rep.Some(nowRank), Rep.Some(hisRank), Rep.Some(likecount), Rep.Some(noReprint), Rep.Some(copyright), Rep.Some(inserttime)).shaped.<>({r=>import r._; _1.map(_=> rVideo.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get, _19.get, _20.get, _21.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column av SqlType(int4), PrimaryKey */
    val av: Rep[Int] = column[Int]("av", O.PrimaryKey)
    /** Database column title SqlType(text) */
    val title: Rep[String] = column[String]("title")
    /** Database column upname SqlType(varchar), Length(1024,true) */
    val upname: Rep[String] = column[String]("upname", O.Length(1024,varying=true))
    /** Database column upmid SqlType(int4) */
    val upmid: Rep[Int] = column[Int]("upmid")
    /** Database column createtime SqlType(timestamp) */
    val createtime: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("createtime")
    /** Database column zone SqlType(varchar), Length(255,true) */
    val zone: Rep[String] = column[String]("zone", O.Length(255,varying=true))
    /** Database column subzone SqlType(varchar), Length(255,true) */
    val subzone: Rep[String] = column[String]("subzone", O.Length(255,varying=true))
    /** Database column cid SqlType(varchar), Length(255,true) */
    val cid: Rep[String] = column[String]("cid", O.Length(255,varying=true))
    /** Database column aid SqlType(int4) */
    val aid: Rep[Int] = column[Int]("aid")
    /** Database column viewcount SqlType(int4) */
    val viewcount: Rep[Int] = column[Int]("viewcount")
    /** Database column danmaku SqlType(int4) */
    val danmaku: Rep[Int] = column[Int]("danmaku")
    /** Database column reply SqlType(int4) */
    val reply: Rep[Int] = column[Int]("reply")
    /** Database column favorite SqlType(int4) */
    val favorite: Rep[Int] = column[Int]("favorite")
    /** Database column coin SqlType(int4) */
    val coin: Rep[Int] = column[Int]("coin")
    /** Database column sharecount SqlType(int4) */
    val sharecount: Rep[Int] = column[Int]("sharecount")
    /** Database column now_rank SqlType(int4) */
    val nowRank: Rep[Int] = column[Int]("now_rank")
    /** Database column his_rank SqlType(int4) */
    val hisRank: Rep[Int] = column[Int]("his_rank")
    /** Database column likecount SqlType(int4) */
    val likecount: Rep[Int] = column[Int]("likecount")
    /** Database column no_reprint SqlType(int4) */
    val noReprint: Rep[Int] = column[Int]("no_reprint")
    /** Database column copyright SqlType(int4) */
    val copyright: Rep[Int] = column[Int]("copyright")
    /** Database column inserttime SqlType(timestamp) */
    val inserttime: Rep[java.sql.Timestamp] = column[java.sql.Timestamp]("inserttime")
  }
  /** Collection-like TableQuery object for table tVideo */
  lazy val tVideo = new TableQuery(tag => new tVideo(tag))
}