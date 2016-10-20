package com.esri

import scala.collection.mutable.ArrayBuffer
import scala.math._

case class DirDist(mx: Double, my: Double, sx: Double, sy: Double, heading: Double, n: Int) {

  def generatePoints(nPoint: Int): Seq[(Double, Double)] = {
    val arr = new ArrayBuffer[(Double, Double)]()
    var t = 0.0
    val dt = 1.0 / nPoint

    val alpha = (90 - heading).toRadians
    val cosA = math.cos(alpha)
    val sinA = math.sin(alpha)
    val maj = sx max sy
    val min = sx min sy
    for (i <- 0 to nPoint) {
      val r = t * 2.0 * math.Pi
      val x = maj * math.cos(r)
      val y = min * math.sin(r)
      val rx = x * cosA - y * sinA
      val ry = x * sinA + y * cosA
      arr.append((mx + rx, my + ry))
      t += dt
    }
    arr
  }
}

object DirDist extends Serializable {
  def apply(iter: Iterable[(Double, Double)], minPoints: Int) = {

    val mu = iter.foldLeft(OnlineMu()) {
      case (prev, (x, y)) =>
        prev.add(x, y)
    }

    if (mu.n > minPoints) {
      val (arr, x2, y2, xy) = mu.deviations(iter)
      if (x2.abs < 0.0000001 || y2.abs < 0.0000001)
        None
      else {
        // val ellipseX = sqrt(x2 / mu.n)
        // val ellipseY = sqrt(y2 / mu.n)

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

        Some(new DirDist(mu.mx, mu.my, sigX, sigY, rad.toDegrees, mu.n))
      }
    } else {
      None
    }
  }
}
