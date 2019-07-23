name := "search"

scalaVersion := "2.12.8"

val circeVersion = "0.11.1"
val monocleVersion = "1.6.0"
val univEqVersion = "1.0.5"
val catsVersion = "1.5.0"
val catsEffectVersion =  "1.3.1"
val jlineVersion = "3.12.1"


libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.github.julien-truffaut" %%  "monocle-core"  % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-macro" % monocleVersion,
  "com.github.japgolly.univeq" %% "univeq" % univEqVersion,
  "com.github.japgolly.univeq" %% "univeq-cats" % univEqVersion,
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.typelevel" %% "cats-effect" % catsEffectVersion,
  "org.jline" % "jline" % jlineVersion,
  "com.github.julien-truffaut" %%  "monocle-law"   % monocleVersion % Test,
  "com.lihaoyi" %% "utest" % "0.7.1" % Test,
)

testFrameworks += new TestFramework("utest.runner.Framework")

scalacOptions += "-Ypartial-unification"

addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")


