name := "FinagleServer"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= List(
  "com.twitter" %% "finagle-httpx" % "6.29.0",
  "com.typesafe.akka" %% "akka-actor" % "2.3.14")