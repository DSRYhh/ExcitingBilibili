name := "ExcitingBilibili"

version := "1.0"

scalaVersion := "2.12.1"

val circeVersion = "0.8.0"


libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.0.9",
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.9" % Test
)

libraryDependencies ++= Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"
