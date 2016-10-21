package com.esri

import org.apache.spark.{Logging, SparkContext}


object MainScanDir extends App with Logging {

  val filename = args.length match {
    case 1 => args.head
    case _ => "app.properties"
  }

  val conf = AppProperties.loadProperties(filename)

  val sc = new SparkContext(conf)
  try {
    val inputPath = conf.get("input.path", "/tmp/points.csv")
    val outputPath = conf.get("output.path", "/tmp/tmp")
    val numPoints = conf.getInt("ellipse.points", 100)
    val minPoints = conf.getInt("min.points", 3)
    val epsilon = conf.getDouble("distance.meters", 300.0)
    val indexKey = conf.getInt("index.caseid", 0)
    val indexLon = conf.getInt("index.longitude", 1)
    val indexLat = conf.getInt("index.latitude", 2)
    val fieldSep = conf.get("field.sep", ",") match {
      case "tab" => "\t"
      case other => other
    }
    sc.textFile(inputPath)
      .flatMap(line => {
        try {
          val splits = line.split(fieldSep, -1)
          val k = splits(indexKey)
          val x = splits(indexLon).toDouble.toMercatorX
          val y = splits(indexLat).toDouble.toMercatorY
          Some(k -> (x, y))
        }
        catch {
          case t: Throwable => None
        }
      })
      .groupByKey()
      .flatMapValues(ScanDir(_, epsilon, minPoints))
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
