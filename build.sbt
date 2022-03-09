name := """money-manager"""
organization := "com.fg"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.8"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies ++= Seq(
  jdbc,
  "org.playframework.anorm" %% "anorm" % "2.6.10",
  "org.playframework.anorm" %% "anorm-postgres" % "2.6.10"
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.fg.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.fg.binders._"
