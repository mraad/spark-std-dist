package com.esri

import scala.math._

case class StdDist(mx: Double, my: Double, sd: Double)

object StdDist extends Serializable {
  def apply(iter: Iterable[(Double, Double)]): StdDist = {
    val (ox, oy) = iter.foldLeft((OnlineVar(), OnlineVar())) {
      case ((ox, oy), (x, y)) => {
        ox += x
        oy += y
        (ox, oy)
      }
    }
    val sd = sqrt(ox.variance + oy.variance)
    StdDist(ox.mu, oy.mu, sd)
  }
}
