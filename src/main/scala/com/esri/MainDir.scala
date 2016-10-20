package com.esri

import org.apache.spark.{Logging, SparkContext}


object MainDir extends App with Logging {

  val filename = args.length match {
    case 1 => args.head
    case _ => "app.properties"
  }

  val conf = AppProperties.loadProperties(filename)
    .registerKryoClasses(Array(
      classOf[OnlineVar],
      classOf[OnlineMu],
      classOf[DirDist]
    ))

  val sc = new SparkContext(conf)
  try {
    val inputPath = conf.get("input.path", "/tmp/points.csv")
    val outputPath = conf.get("output.path", "/tmp/tmp")
    val minPoints = conf.getInt("min.points", 1)
    val numPoints = conf.getInt("ellipse.points", 100)
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
      .flatMapValues(DirDist(_, minPoints))
      .map {
        case (caseId, dirDist) => {
          val wkt = dirDist.generatePoints(numPoints).map {
            case (x, y) => s"$x $y"
          }
            .mkString("POLYGON((", ",", "))")
          s"$caseId\t${dirDist.mx}\t${dirDist.my}\t${dirDist.heading}\t${dirDist.sx}\t${dirDist.sy}\t$wkt"
        }
      }
      .saveAsTextFile(outputPath)
  } finally {
    sc.stop()
  }
}
