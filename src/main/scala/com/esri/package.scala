package com

/**
  */
package object esri {

  implicit class DoubleImplicits(n: Double) {
    def toMercatorX() = WebMercator.longitudeToX(n)

    def toMercatorY() = WebMercator.latitudeToY(n)

    def toLongitude() = WebMercator.xToLongitude(n)

    def toLatitude() = WebMercator.yToLatitude(n)

    def toHeadingRadians() = {
      val deg = if (n <= 90.0 || n > 180.0) {
        360.0 - (n + 270.0) % 360.0
      }
      else {
        (-270.0 - n) % 360.0
      }
      deg.toRadians
    }
  }

}
