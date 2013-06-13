name := "textconverter"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
    "edu.washington.cs.knowitall.nlptools" %% "nlptools-sentence-opennlp" % "2.4.2",
    "commons-io" % "commons-io" % "2.4",
    "com.github.scopt" %% "scopt" % "2.1.0",
    "org.apache.tika" % "tika-core" % "1.3",  
    "org.apache.tika" % "tika-parsers" % "1.3")

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
