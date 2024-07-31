name := "spark-stepik"

version := "0.1"

scalaVersion := "2.12.15" //"2.13.8"


lazy val sparkVersion = "3.2.1"


libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "io.circe" %% "circe-core" % "0.14.5",
  "io.circe" %% "circe-generic" % "0.14.5",
  "io.circe" %% "circe-parser" % "0.14.5"

)

SettingKey[Option[String]]("ide-package-prefix") := Option("com.example")