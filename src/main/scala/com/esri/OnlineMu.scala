package com.esri

case class OnlineMu(var n: Int = 0, var mx: Double = 0.0, var my: Double = 0.0) {

  def add(x: Double, y: Double) = {
    n += 1

    val dx = x - mx
    mx += dx / n

    val dy = y - my
    my += dy / n

    this
  }

  def deviations(xy: Iterable[(Double, Double)]) = {
    xy.foldLeft((Array.empty[(Double, Double)], 0.0, 0.0, 0.0)) {
      case ((arr, x2, y2, xy), (x, y)) => {
        val dx = x - mx
        val dy = y - my
        (arr :+(dx, dy), x2 + dx * dx, y2 + dy * dy, xy + dx * dy)
      }
    }
  }
}
