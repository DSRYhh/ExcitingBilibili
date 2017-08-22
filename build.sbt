
name := "ExcitingBilibili"

version := "1.0"

scalaVersion := "2.12.1"

val circeVersion = "0.8.0"
val akkaVersion = "2.5.4"
libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.0.9",
    "com.typesafe.slick" %% "slick" % "3.2.0",
    "com.typesafe.slick" %% "slick-codegen" % "3.2.0",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.slf4j" % "slf4j-api" % "1.7.25",
    "org.postgresql" % "postgresql" % "9.3-1100-jdbc4",
    "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
    "org.scala-lang" % "scala-library" % scalaVersion.value,
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "com.zaxxer" % "HikariCP" % "2.6.1",
    "org.jsoup" % "jsoup" % "1.8.3",
    "org.apache.commons" % "commons-text" % "1.1"
)


libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
).map(_ % circeVersion)

