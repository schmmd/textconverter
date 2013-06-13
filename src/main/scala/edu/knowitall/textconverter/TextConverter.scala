package edu.knowitall.textconverter

import java.io.File
import scopt.immutable
import org.apache.commons.io.FileUtils
import scala.collection.JavaConverters._
import org.apache.tika.Tika
import edu.knowitall.common.Resource
import scala.io.Source
import java.io.PrintWriter
import java.io.IOException
import org.apache.tika.exception.TikaException
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import edu.knowitall.tool.sentence.OpenNlpSentencer

object SentenceSplitter extends App {
  case class Config(
    inputFile: File = null,
    outputFile: File = null)

  val parser = new immutable.OptionParser[Config]("textconverter") {
    def options = Seq(
      argOpt("input file", "input file") { (string, config) =>
        val file = new File(string)
        require(file.exists, "input file does not exist: " + file)
        config.copy(inputFile = file)
      },
      argOpt("output file", "output file") { (string, config) =>
        val file = new File(string)
        config.copy(outputFile = file)
      })
  }

  parser.parse(args, Config()) match {
    case Some(config) => run(config)
    case None =>
  }

  def sentenceFilter(sentence: String) = {
    val terminatingCharacters = Set('.', '?', '!')
    sentence.length > 5 && terminatingCharacters(sentence.last) && sentence.length < 400
  }

  def sentenceMap(sentence: String) = {
    sentence.trim.replaceAll("\t", " ")
  }

  def run(config: Config) {
    val sentencer = new OpenNlpSentencer()
    val inputFiles = FileUtils.listFiles(config.inputFile, Array("txt"), true)

    for (inputFile <- inputFiles.asScala) {
      println("Processing: " + inputFile)
      val outputFileName = inputFile.getName() + ".sentences"
      val outputFile = new File(config.outputFile, outputFileName)

      if (!outputFile.exists()) {
        Resource.using(Source.fromFile(inputFile, "UTF-8")) { source =>
          val lines = source.getLines.buffered
          Resource.using(new PrintWriter(outputFile, "UTF-8")) { writer =>
            while (lines.hasNext) {
              var segment: Vector[String] = Vector.empty
              while (lines.hasNext && !lines.head.trim.isEmpty) {
                segment = segment :+ lines.next
              }

              // skip over whitespace line
              if (lines.hasNext) lines.next

	          val sentences = sentencer(segment.mkString(" "))
	          sentences.iterator.map(_.text).map(sentenceMap).filter(sentenceFilter) foreach writer.println
            }

	        println("Written to: " + outputFile)
          }
        }
      }
      else {
        println("Skipping because output file exists: " + inputFile)
      }

      println()
    }
  }
}
