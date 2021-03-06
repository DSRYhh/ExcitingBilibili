package ExcitingBilibili.Utility.Database

import ExcitingBilibili.Utility.AppSettings
import slick.codegen.SourceCodeGenerator
import slick.jdbc.JdbcProfile

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * User: Taoz
  * Date: 7/15/2015
  * Time: 9:33 AM
  */
object MySlickCodeGenerator {


  import concurrent.ExecutionContext.Implicits.global

  private val slickDriver = "slick.jdbc.PostgresProfile"
  private val jdbcDriver = "org.postgresql.Driver"
  private val url = AppSettings.slickUrl
  private val outputFolder = "/src/main/scala"
  private val pkg = "ExcitingBilibili.Utility.Database"
  private val user = AppSettings.slickUser
  private val password = AppSettings.slickPassword

  def genDefaultTables(): Unit = {

    slick.codegen.SourceCodeGenerator.main(
      Array(slickDriver, jdbcDriver, url, outputFolder, pkg, user, password)
    )


  }

  def main(args: Array[String]) {
    //genDefaultTables()
    val dbDriver = slick.jdbc.PostgresProfile

    genCustomTables(dbDriver)

    println(s"Tables.scala generated in $outputFolder")

  }

  def genCustomTables(dbDriver: JdbcProfile): Unit = {

    // fetch data model
    val driver: JdbcProfile =
      Class.forName(slickDriver + "$").getField("MODULE$").get(null).asInstanceOf[JdbcProfile]
    val dbFactory = driver.api.Database
    val db = dbFactory.forURL(url, driver = jdbcDriver,
      user = user, password = password, keepAliveConnection = true)


    // fetch data model
    val modelAction = dbDriver.createModel(Some(dbDriver.defaultTables)) // you can filter specific tables here
    val modelFuture = db.run(modelAction)

    // customize code generator
    val codeGenFuture = modelFuture.map(model => new SourceCodeGenerator(model) {
      // override mapped table and class name
      override def entityName: (String) => String =
        dbTableName => "r" + dbTableName.toCamelCase

      override def tableName: (String) => String =
        dbTableName => "t" + dbTableName.toCamelCase

      // add some custom import
      // override def code = "import foo.{MyCustomType,MyCustomTypeMapper}" + "\n" + super.code

      // override table generator
      /*    override def Table = new Table(_){
            // disable entity class generation and mapping
            override def EntityType = new EntityType{
              override def classEnabled = false
            }

            // override contained column generator
            override def Column = new Column(_){
              // use the data model member of this column to change the Scala type,
              // e.g. to a custom enum or anything else
              override def rawType =
                if(model.name == "SOME_SPECIAL_COLUMN_NAME") "MyCustomType" else super.rawType
            }
          }*/
    })

    val codeGenerator = Await.result(codeGenFuture, Duration.Inf)
    codeGenerator.writeToFile(
      slickDriver, outputFolder, pkg, "Tables", "Tables.scala"
    )


  }


}


