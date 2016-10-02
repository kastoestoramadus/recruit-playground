name := "FinagleServer"

version := "1.0"

scalaVersion := "2.11.8"

mainClass in (Compile, run) := Some("walidus.cshop.ShopTerminalInterface")

libraryDependencies ++= List(
"com.twitter" %% "finagle-httpx" % "6.29.0",
"com.typesafe.akka" %% "akka-actor" % "2.3.14",
"org.scala-graph" %% "graph-core" % "1.11.2",
"org.scalatest" % "scalatest_2.11" % "3.0.0" % "test"
)

