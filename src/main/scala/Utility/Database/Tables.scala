package Utility.Database

// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.PostgresProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = tComments.schema ++ tDanmu.schema ++ tVideo.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table tComments
   *  @param av Database column av SqlType(int4)
   *  @param mid Database column mid SqlType(int4)
   *  @param lv Database column lv SqlType(int4)
   *  @param fbid Database column fbid SqlType(varchar), Length(255,true)
   *  @param adCheck Database column ad_check SqlType(int4)
   *  @param good Database column good SqlType(int4)
   *  @param isgood Database column isgood SqlType(int4)
   *  @param msg Database column msg SqlType(text)
   *  @param device Database column device SqlType(varchar), Length(255,true)
   *  @param createnum Database column createnum SqlType(int4)
   *  @param createAt Database column create_at SqlType(varchar), Length(255,true)
   *  @param replyCount Database column reply_count SqlType(int4)
   *  @param face Database column face SqlType(varchar), Length(255,true)
   *  @param rank Database column rank SqlType(int4)
   *  @param nick Database column nick SqlType(varchar), Length(255,true)
   *  @param currentExp Database column current_exp SqlType(int4)
   *  @param currentLevel Database column current_level SqlType(int4)
   *  @param currentMin Database column current_min SqlType(int4)
   *  @param nextExp Database column next_exp SqlType(int4)
   *  @param sex Database column sex SqlType(varchar), Length(255,true)
   *  @param parentfeedbackid Database column parentfeedbackid SqlType(varchar), Length(255,true) */
  case class rComments(av: Int, mid: Int, lv: Int, fbid: String, adCheck: Int, good: Int, isgood: Int, msg: String, device: String, createnum: Int, createAt: String, replyCount: Int, face: String, rank: Int, nick: String, currentExp: Int, currentLevel: Int, currentMin: Int, nextExp: Int, sex: String, parentfeedbackid: String)
  /** GetResult implicit for fetching rComments objects using plain SQL queries */
  implicit def GetResultrComments(implicit e0: GR[Int], e1: GR[String]): GR[rComments] = GR{
    prs => import prs._
    rComments.tupled((<<[Int], <<[Int], <<[Int], <<[String], <<[Int], <<[Int], <<[Int], <<[String], <<[String], <<[Int], <<[String], <<[Int], <<[String], <<[Int], <<[String], <<[Int], <<[Int], <<[Int], <<[Int], <<[String], <<[String]))
  }
  /** Table description of table comments. Objects of this class serve as prototypes for rows in queries. */
  class tComments(_tableTag: Tag) extends profile.api.Table[rComments](_tableTag, "comments") {
    def * = (av, mid, lv, fbid, adCheck, good, isgood, msg, device, createnum, createAt, replyCount, face, rank, nick, currentExp, currentLevel, currentMin, nextExp, sex, parentfeedbackid) <> (rComments.tupled, rComments.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(av), Rep.Some(mid), Rep.Some(lv), Rep.Some(fbid), Rep.Some(adCheck), Rep.Some(good), Rep.Some(isgood), Rep.Some(msg), Rep.Some(device), Rep.Some(createnum), Rep.Some(createAt), Rep.Some(replyCount), Rep.Some(face), Rep.Some(rank), Rep.Some(nick), Rep.Some(currentExp), Rep.Some(currentLevel), Rep.Some(currentMin), Rep.Some(nextExp), Rep.Some(sex), Rep.Some(parentfeedbackid)).shaped.<>({r=>import r._; _1.map(_=> rComments.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get, _19.get, _20.get, _21.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column av SqlType(int4) */
    val av: Rep[Int] = column[Int]("av")
    /** Database column mid SqlType(int4) */
    val mid: Rep[Int] = column[Int]("mid")
    /** Database column lv SqlType(int4) */
    val lv: Rep[Int] = column[Int]("lv")
    /** Database column fbid SqlType(varchar), Length(255,true) */
    val fbid: Rep[String] = column[String]("fbid", O.Length(255,varying=true))
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
    /** Database column createnum SqlType(int4) */
    val createnum: Rep[Int] = column[Int]("createnum")
    /** Database column create_at SqlType(varchar), Length(255,true) */
    val createAt: Rep[String] = column[String]("create_at", O.Length(255,varying=true))
    /** Database column reply_count SqlType(int4) */
    val replyCount: Rep[Int] = column[Int]("reply_count")
    /** Database column face SqlType(varchar), Length(255,true) */
    val face: Rep[String] = column[String]("face", O.Length(255,varying=true))
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
  }
  /** Collection-like TableQuery object for table tComments */
  lazy val tComments = new TableQuery(tag => new tComments(tag))

  /** Entity class storing rows of table tDanmu
   *  @param cid Database column cid SqlType(int4), Default(None)
   *  @param av Database column av SqlType(int4), Default(None)
   *  @param danmu Database column danmu SqlType(text), Default(None) */
  case class rDanmu(cid: Option[Int] = None, av: Option[Int] = None, danmu: Option[String] = None)
  /** GetResult implicit for fetching rDanmu objects using plain SQL queries */
  implicit def GetResultrDanmu(implicit e0: GR[Option[Int]], e1: GR[Option[String]]): GR[rDanmu] = GR{
    prs => import prs._
    rDanmu.tupled((<<?[Int], <<?[Int], <<?[String]))
  }
  /** Table description of table danmu. Objects of this class serve as prototypes for rows in queries. */
  class tDanmu(_tableTag: Tag) extends profile.api.Table[rDanmu](_tableTag, "danmu") {
    def * = (cid, av, danmu) <> (rDanmu.tupled, rDanmu.unapply)

    /** Database column cid SqlType(int4), Default(None) */
    val cid: Rep[Option[Int]] = column[Option[Int]]("cid", O.Default(None))
    /** Database column av SqlType(int4), Default(None) */
    val av: Rep[Option[Int]] = column[Option[Int]]("av", O.Default(None))
    /** Database column danmu SqlType(text), Default(None) */
    val danmu: Rep[Option[String]] = column[Option[String]]("danmu", O.Default(None))
  }
  /** Collection-like TableQuery object for table tDanmu */
  lazy val tDanmu = new TableQuery(tag => new tDanmu(tag))

  /** Entity class storing rows of table tVideo
   *  @param av Database column av SqlType(varchar), PrimaryKey, Length(255,true)
   *  @param title Database column title SqlType(varchar), Length(255,true)
   *  @param upname Database column upname SqlType(varchar), Length(255,true)
   *  @param upmid Database column upmid SqlType(int4)
   *  @param createtime Database column createtime SqlType(varchar), Length(255,true)
   *  @param zone Database column zone SqlType(varchar), Length(255,true)
   *  @param subzone Database column subzone SqlType(varchar), Length(255,true)
   *  @param cid Database column cid SqlType(varchar), Length(255,true)
   *  @param aid Database column aid SqlType(int4)
   *  @param viewtime Database column viewtime SqlType(int4)
   *  @param danmaku Database column danmaku SqlType(int4)
   *  @param reply Database column reply SqlType(int4)
   *  @param favorite Database column favorite SqlType(int4)
   *  @param coin Database column coin SqlType(int4)
   *  @param sharenum Database column sharenum SqlType(int4)
   *  @param nowRank Database column now_rank SqlType(int4)
   *  @param hisRank Database column his_rank SqlType(int4)
   *  @param likenum Database column likenum SqlType(int4)
   *  @param noReprint Database column no_reprint SqlType(int4)
   *  @param copyright Database column copyright SqlType(int4) */
  case class rVideo(av: String, title: String, upname: String, upmid: Int, createtime: String, zone: String, subzone: String, cid: String, aid: Int, viewtime: Int, danmaku: Int, reply: Int, favorite: Int, coin: Int, sharenum: Int, nowRank: Int, hisRank: Int, likenum: Int, noReprint: Int, copyright: Int)
  /** GetResult implicit for fetching rVideo objects using plain SQL queries */
  implicit def GetResultrVideo(implicit e0: GR[String], e1: GR[Int]): GR[rVideo] = GR{
    prs => import prs._
    rVideo.tupled((<<[String], <<[String], <<[String], <<[Int], <<[String], <<[String], <<[String], <<[String], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int], <<[Int]))
  }
  /** Table description of table video. Objects of this class serve as prototypes for rows in queries. */
  class tVideo(_tableTag: Tag) extends profile.api.Table[rVideo](_tableTag, "video") {
    def * = (av, title, upname, upmid, createtime, zone, subzone, cid, aid, viewtime, danmaku, reply, favorite, coin, sharenum, nowRank, hisRank, likenum, noReprint, copyright) <> (rVideo.tupled, rVideo.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(av), Rep.Some(title), Rep.Some(upname), Rep.Some(upmid), Rep.Some(createtime), Rep.Some(zone), Rep.Some(subzone), Rep.Some(cid), Rep.Some(aid), Rep.Some(viewtime), Rep.Some(danmaku), Rep.Some(reply), Rep.Some(favorite), Rep.Some(coin), Rep.Some(sharenum), Rep.Some(nowRank), Rep.Some(hisRank), Rep.Some(likenum), Rep.Some(noReprint), Rep.Some(copyright)).shaped.<>({r=>import r._; _1.map(_=> rVideo.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get, _9.get, _10.get, _11.get, _12.get, _13.get, _14.get, _15.get, _16.get, _17.get, _18.get, _19.get, _20.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column av SqlType(varchar), PrimaryKey, Length(255,true) */
    val av: Rep[String] = column[String]("av", O.PrimaryKey, O.Length(255,varying=true))
    /** Database column title SqlType(varchar), Length(255,true) */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true))
    /** Database column upname SqlType(varchar), Length(255,true) */
    val upname: Rep[String] = column[String]("upname", O.Length(255,varying=true))
    /** Database column upmid SqlType(int4) */
    val upmid: Rep[Int] = column[Int]("upmid")
    /** Database column createtime SqlType(varchar), Length(255,true) */
    val createtime: Rep[String] = column[String]("createtime", O.Length(255,varying=true))
    /** Database column zone SqlType(varchar), Length(255,true) */
    val zone: Rep[String] = column[String]("zone", O.Length(255,varying=true))
    /** Database column subzone SqlType(varchar), Length(255,true) */
    val subzone: Rep[String] = column[String]("subzone", O.Length(255,varying=true))
    /** Database column cid SqlType(varchar), Length(255,true) */
    val cid: Rep[String] = column[String]("cid", O.Length(255,varying=true))
    /** Database column aid SqlType(int4) */
    val aid: Rep[Int] = column[Int]("aid")
    /** Database column viewtime SqlType(int4) */
    val viewtime: Rep[Int] = column[Int]("viewtime")
    /** Database column danmaku SqlType(int4) */
    val danmaku: Rep[Int] = column[Int]("danmaku")
    /** Database column reply SqlType(int4) */
    val reply: Rep[Int] = column[Int]("reply")
    /** Database column favorite SqlType(int4) */
    val favorite: Rep[Int] = column[Int]("favorite")
    /** Database column coin SqlType(int4) */
    val coin: Rep[Int] = column[Int]("coin")
    /** Database column sharenum SqlType(int4) */
    val sharenum: Rep[Int] = column[Int]("sharenum")
    /** Database column now_rank SqlType(int4) */
    val nowRank: Rep[Int] = column[Int]("now_rank")
    /** Database column his_rank SqlType(int4) */
    val hisRank: Rep[Int] = column[Int]("his_rank")
    /** Database column likenum SqlType(int4) */
    val likenum: Rep[Int] = column[Int]("likenum")
    /** Database column no_reprint SqlType(int4) */
    val noReprint: Rep[Int] = column[Int]("no_reprint")
    /** Database column copyright SqlType(int4) */
    val copyright: Rep[Int] = column[Int]("copyright")
  }
  /** Collection-like TableQuery object for table tVideo */
  lazy val tVideo = new TableQuery(tag => new tVideo(tag))
}
