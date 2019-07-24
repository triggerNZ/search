name := "search"

scalaVersion := "2.12.8"

val circeVersion = "0.11.1"
val univEqVersion = "1.0.5"
val catsVersion = "1.5.0"
val jlineVersion = "3.12.1"


libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.github.japgolly.univeq" %% "univeq" % univEqVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.jline" % "jline" % jlineVersion,
  "com.lihaoyi" %% "utest" % "0.7.1" % Test,
)

testFrameworks += new TestFramework("utest.runner.Framework")

scalacOptions += "-Ypartial-unification"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")


