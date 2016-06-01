package com.esri

import scala.math._

case class DirDist(mx: Double, my: Double, sx: Double, sy: Double, degrees: Double)

object DirDist extends Serializable {
  def apply(iter: Iterable[(Double, Double)]) = {

    val mu = iter.foldLeft(OnlineMu()) {
      case (prev, (x, y)) =>
        prev.add(x, y)
    }

    val (arr, x2, y2, xy) = mu.deviations(iter)

    /*
        val ellipseX = sqrt(x2 / mu.n)
        val ellipseY = sqrt(y2 / mu.n)
    */

    val a = x2 - y2
    val b = sqrt(a * a + 4 * xy * xy)
    val c = 2.0 * xy

    val ang = atan((a + b) / c)
    val rad = if (ang < 0.0) Pi + ang else ang
    val deg = rad.toDegrees
    val flip = deg > 45 && deg < 135

    val ca = cos(ang)
    val sa = sin(ang)

    val (sx, sy) = arr.foldLeft((0.0, 0.0)) {
      case ((sx, sy), (x, y)) => {
        val dx = x * ca - y * sa
        val dy = x * sa + y * ca
        (sx + dx * dx, sy + dy * dy)
      }
    }

    val (tx, ty) = if (flip) (sy, sx) else (sx, sy)

    val sigX = sqrt(2.0) * sqrt(tx / mu.n)
    val sigY = sqrt(2.0) * sqrt(ty / mu.n)

    new DirDist(mu.mx, mu.my, sigX, sigY, rad.toDegrees)
  }
}
