CREATE TABLE IF NOT EXISTS Comments (
  av               INT          NOT NULL,
  mid              INT          NOT NULL,
  lv               INT          NOT NULL,
  fbid             VARCHAR(255) NOT NULL,
  ad_check         INT          NOT NULL,
  good             INT          NOT NULL,
  isgood           INT          NOT NULL,
  msg              TEXT         NOT NULL,
  device           VARCHAR(255) NOT NULL,
  createUnixTime   TIMESTAMP    NOT NULL,
  create_at        VARCHAR(255) NOT NULL,
  reply_count      INT          NOT NULL,
  face             TEXT         NOT NULL,
  rank             INT          NOT NULL,
  nick             VARCHAR(255) NOT NULL,
  current_exp      INT          NOT NULL,
  current_level    INT          NOT NULL,
  current_min      INT          NOT NULL,
  next_exp         INT          NOT NULL,
  sex              VARCHAR(255) NOT NULL,
  parentFeedBackId VARCHAR(255) NOT NULL,
  insertTime       TIMESTAMP    NOT NULL,
  PRIMARY KEY (fbid)
);
CREATE TABLE IF NOT EXISTS Video (
  av         INT           NOT NULL,
  title      TEXT          NOT NULL,
  upName     VARCHAR(1024) NOT NULL,
  upMid      INT           NOT NULL,
  createTime TIMESTAMP     NOT NULL,
  zone       VARCHAR(255)  NOT NULL,
  subZone    VARCHAR(255)  NOT NULL,
  cid        VARCHAR(255)  NOT NULL,
  aid        INT           NOT NULL,
  viewCount  INT           NOT NULL,
  danmaku    INT           NOT NULL,
  reply      INT           NOT NULL,
  favorite   INT           NOT NULL,
  coin       INT           NOT NULL,
  shareCount INT           NOT NULL,
  now_rank   INT           NOT NULL,
  his_rank   INT           NOT NULL,
  likeCount  INT           NOT NULL,
  no_reprINT INT           NOT NULL,
  copyright  INT           NOT NULL,
  insertTime TIMESTAMP     NOT NULL,
  PRIMARY KEY (av)
);

CREATE TABLE IF NOT EXISTS Danmu (
  cid        INT       NOT NULL,
  av         INT       NOT NULL,
  danmu      TEXT      NOT NULL,
  sendingTimeInVideo DOUBLE PRECISION	 NOT NULL,
  danmuType INT NOT NULL ,
  fontSize INT NOT NULL ,
  fontColor BIGINT NOT NULL ,
  sendingTime TIMESTAMP NOT NULL ,
  poolSize INT NOT NULL ,
  senderId VARCHAR(255) NOT NULL ,
  danmuId BIGINT NOT NULL ,
  insertTime TIMESTAMP NOT NULL,
  PRIMARY KEY (danmuId)
);

CREATE TABLE IF NOT EXISTS TraversalLog (
  index       SERIAL,
  av          INT       NOT NULL,
  operateTime TIMESTAMP NOT NULL,
  PRIMARY KEY (index)
);