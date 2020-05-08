name := "temp"

version := "0.1"

scalaVersion := "2.12.10"
val circeVersion = "0.13.0"

libraryDependencies += "io.circe" %% "circe-core" % circeVersion
libraryDependencies += "io.circe" %% "circe-generic" % circeVersion
libraryDependencies += "io.circe" %% "circe-parser" % circeVersion
libraryDependencies += "io.circe" %% "circe-optics" % circeVersion
