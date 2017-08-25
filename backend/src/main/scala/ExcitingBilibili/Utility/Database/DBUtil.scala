package ExcitingBilibili.Utility.Database

import ExcitingBilibili.Utility.AppSettings
import org.slf4j.{Logger, LoggerFactory}
import slick.jdbc.PostgresProfile.api._
import com.zaxxer.hikari.HikariDataSource
import slick.jdbc.PostgresProfile

/**
  * User: Taoz
  * Date: 2/9/2015
  * Time: 4:33 PM
  */
object DBUtil
{
  val log: Logger = LoggerFactory.getLogger(this.getClass)
  private val dataSource = createDataSource()

  private def createDataSource() =
  {

    val dataSource = new org.postgresql.ds.PGSimpleDataSource()

    log.info(s"connect to db: ${AppSettings.slickUrl}")
    dataSource.setUrl(AppSettings.slickUrl)
    dataSource.setUser(AppSettings.slickUser)
    dataSource.setPassword(AppSettings.slickPassword)

    val hikariDS = new HikariDataSource()
    hikariDS.setDataSource(dataSource)
    hikariDS.setMaximumPoolSize(AppSettings.slickMaximumPoolSize)
    hikariDS.setConnectionTimeout(AppSettings.slickConnectTimeout)
    hikariDS.setIdleTimeout(AppSettings.slickIdleTimeout)
    hikariDS.setMaxLifetime(AppSettings.slickMaxLifetime)
    hikariDS.setAutoCommit(true)
    hikariDS
  }

  val db: PostgresProfile.backend.DatabaseDef = Database.forDataSource(dataSource, Some(AppSettings.slickMaximumPoolSize))
}