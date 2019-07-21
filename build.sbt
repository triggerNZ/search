name := "search"

scalaVersion := "2.12.8"

val circeVersion = "0.11.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies += "com.lihaoyi" %% "utest" % "0.7.1" % Test

testFrameworks += new TestFramework("utest.runner.Framework")

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")


