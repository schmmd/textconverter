name := "textconverter"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
    "edu.washington.cs.knowitall.common-scala" %% "common-scala" % "1.1.1",
    "commons-io" % "commons-io" % "2.4",
    "com.github.scopt" %% "scopt" % "2.1.0",
    "org.apache.tika" % "tika-core" % "1.3",  
    "org.apache.tika" % "tika-parsers" % "1.3")
