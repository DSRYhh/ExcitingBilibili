CREATE TABLE IF NOT EXISTS Comments (
  av               INT NOT NULL,
  mid              INT NOT NULL,
  lv               INT NOT NULL,
  fbid             VARCHAR(255) NOT NULL,
  ad_check         INT NOT NULL,
  good             INT NOT NULL,
  isgood           INT NOT NULL,
  msg              TEXT NOT NULL,
  device           VARCHAR(255) NOT NULL,
  createNum        INT NOT NULL,
  create_at        VARCHAR(255) NOT NULL,
  reply_count      INT NOT NULL,
  face             VARCHAR(255) NOT NULL,
  rank             INT NOT NULL,
  nick             VARCHAR(255) NOT NULL,
  current_exp      INT NOT NULL,
  current_level    INT NOT NULL,
  current_min      INT NOT NULL,
  next_exp         INT NOT NULL,
  sex              VARCHAR(255) NOT NULL,
  parentFeedBackId VARCHAR(255) NOT NULL
);
CREATE TABLE IF NOT EXISTS Video (
  av         VARCHAR(255) NOT NULL,
  title      VARCHAR(255) NOT NULL,
  upName     VARCHAR(255) NOT NULL,
  upMid      INT NOT NULL,
  createTime VARCHAR(255) NOT NULL,
  zone       VARCHAR(255) NOT NULL,
  subZone    VARCHAR(255) NOT NULL,
  cid        VARCHAR(255) NOT NULL,
  aid        INT NOT NULL,
  viewTime   INT NOT NULL,
  danmaku    INT NOT NULL,
  reply      INT NOT NULL,
  favorite   INT NOT NULL,
  coin       INT NOT NULL,
  shareNum   INT NOT NULL,
  now_rank   INT NOT NULL,
  his_rank   INT NOT NULL,
  likeNum    INT NOT NULL,
  no_reprINT INT NOT NULL,
  copyright  INT NOT NULL,
  PRIMARY KEY (av)
);

CREATE TABLE IF NOT EXISTS Danmu (
  cid   INT,
  av    INT,
  danmu TEXT
);