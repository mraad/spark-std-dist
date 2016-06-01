package com.esri

import scala.math._

/**
  * https://rosettacode.org/wiki/Haversine_formula#Scala
  */
object Haversine extends Serializable {
  val R = 2.0 * 6378137.0 //meters

  def haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double) = {

    val dLat = (lat2 - lat1).toRadians
    val dLon = (lon2 - lon1).toRadians

    val dy = sin(dLat * 0.5)
    val dx = sin(dLon * 0.5)
    val a = dy * dy + dx * dx * cos(lat1.toRadians) * cos(lat2.toRadians)

    R * asin(sqrt(a))
  }

  /*
    def main(args: Array[String]): Unit = {
      println(haversine(36.12, -86.67, 33.94, -118.40))
    }
  */
}
