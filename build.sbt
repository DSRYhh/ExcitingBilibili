
val projectName = "ExcitingBilibili"
val projectVersion = "1.0"
val scalaCVersion = "2.12.2"

val circeVersion = "0.8.0"
val akkaVersion = "2.5.4"
val slickVersion = "3.2.0"

def commonSettings = Seq(
  version := projectVersion,
  scalaVersion := scalaCVersion,
  scalacOptions ++= Seq(
    //"-deprecation",
    "-feature"
  )
)



lazy val root =  (project in file("."))
  .aggregate(backend)


val backendMainClass = "ExcitingBilibili.Main"

// Akka Http based backend
lazy val backend = (project in file("backend"))
  .settings(commonSettings: _*)
  .settings(
    Revolver.settings.settings,
    mainClass in Revolver.reStart := Some(backendMainClass)
  )
  .settings(name := "backend")
  .settings(
    //pack
    // If you need to specify main classes manually, use packSettings and packMain
    packSettings,
    // [Optional] Creating `hello` command that calls org.mydomain.Hello#main(Array[String])
    packMain := Map(projectName -> backendMainClass),
    packJvmOpts := Map(projectName -> Seq("-Xmx256m", "-Xms64m")),
    packExtraClasspath := Map(projectName -> Seq("."))
  )
  .settings(
    libraryDependencies ++= Seq(
        "com.typesafe.akka" %% "akka-http" % "10.0.9",
        "com.typesafe.slick" %% "slick" % slickVersion,
        "com.typesafe.slick" %% "slick-codegen" % slickVersion,
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "org.slf4j" % "slf4j-api" % "1.7.25",
        "org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
        "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
        "org.scala-lang" % "scala-library" % scalaVersion.value,
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        "com.zaxxer" % "HikariCP" % "2.6.1",
        "org.jsoup" % "jsoup" % "1.8.3",
        "org.apache.commons" % "commons-text" % "1.1",
        "io.circe" %% "circe-core" % circeVersion,
        "io.circe" %% "circe-generic" % circeVersion,
        "io.circe" %% "circe-parser" % circeVersion
    )
  )