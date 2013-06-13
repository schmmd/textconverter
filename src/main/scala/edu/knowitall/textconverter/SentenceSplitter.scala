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

object TextConverter extends App {
  case class Config(
    inputFile: File = null,
    outputFile: File = null,
    exts: Array[String] = Array("txt", "htm", "html", "shtml", "doc", "pdf", "ppt", "docx"))

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

  def run(config: Config) {
    val tika = new Tika()
    val inputFiles = FileUtils.listFiles(config.inputFile, config.exts ++ config.exts.map(_.toUpperCase), true)

    for (inputFile <- inputFiles.asScala) {
      println("Processing: " + inputFile)
      val subdirectory = inputFile.getPath.drop(config.inputFile.getPath.size)
      val outputFileName = inputFile.getName() + ".txt"
      val outputFile = new File(new File(config.outputFile, subdirectory), outputFileName)

      if (!outputFile.exists()) {
        Resource.using(new PrintWriter(outputFile, "UTF-8")) { writer =>
          val text = Try(tika.parseToString(inputFile))
          text match {
            case Success(text) => writer.print(text); println("Written to: " + outputFile)
            case Failure(ex) => ex.printStackTrace()
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
