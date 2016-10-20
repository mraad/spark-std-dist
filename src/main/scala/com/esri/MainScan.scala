package com.esri

import org.apache.spark.{Logging, SparkContext}

import scala.math._


object MainScan extends App with Logging {

  val filename = args.length match {
    case 1 => args.head
    case _ => "app.properties"
  }

  val conf = AppProperties.loadProperties(filename)
    .registerKryoClasses(Array(
      classOf[OnlineVar],
      classOf[OnlineMu],
      classOf[StdDist]
    ))

  val sc = new SparkContext(conf)
  try {
    val inputPath = conf.get("input.path", "/tmp/points.csv")
    val outputPath = conf.get("output.path", "/tmp/tmp")
    val minPoints = conf.getInt("min.points", 3)
    val epsilon = conf.getDouble("distance.epsilon", 300.0) // meters
    sc.textFile(inputPath)
      .flatMap(line => {
        try {
          val splits = line.split(',')
          val k = splits(0)
          val x = splits(1).toDouble.toMercatorX
          val y = splits(2).toDouble.toMercatorY
          Some(k -> (x, y))
        }
        catch {
          case t: Throwable => None
        }
      })
      .groupByKey()
      .flatMapValues(ScanDist(_, epsilon, minPoints))
      .map {
        case (caseId, stdDist) => {
          val wkt = (for (a <- 0 to 360 by 10) yield {
            val d = a.toDouble.toRadians
            val c = stdDist.mx + stdDist.sd * cos(d)
            val s = stdDist.my + stdDist.sd * sin(d)
            s"$c $s"
          }).mkString("POLYGON((", ",", "))")
          s"$caseId\t${stdDist.mx}\t${stdDist.my}\t${stdDist.sd}\t$wkt"
        }
      }
      .saveAsTextFile(outputPath)
  } finally {
    sc.stop()
  }
}
