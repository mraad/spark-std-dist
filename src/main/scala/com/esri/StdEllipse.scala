package com.esri

import scala.collection.mutable.ArrayBuffer

/**
  * @deprecated
  */
case class StdEllipse(mx: Double, my: Double, angle: Double, sx: Double, sy: Double)

/**
  * @deprecated
  */
object StdEllipse extends Serializable {
  def apply(iter: Iterable[(Double, Double)]) = {

    val arrXY = new ArrayBuffer[(Double, Double)]()

    var sx = 0.0
    var sy = 0.0
    var n = 0

    iter.foreach {
      case (x, y) => {
        arrXY.append((x, y))

        sx += x
        sy += y
        n += 1
      }
    }

    val mx = sx / n
    val my = sy / n

    var dx = 0.0
    var dy = 0.0

    val delXY = new ArrayBuffer[(Double, Double)]()

    arrXY.foreach {
      case (x, y) => {
        val lx = x - mx
        val ly = y - my
        delXY.append((lx, ly))
        dx += lx * lx
        dy += ly * ly
      }
    }

    // val sdeX = math.sqrt(dx / n)
    // val sdeY = math.sqrt(dy / n)

    sx = 0.0
    sy = 0.0
    var pp = 0.0

    delXY.foreach {
      case (lx, ly) => {
        sx += lx * lx
        sy += ly * ly
        pp += lx * ly
      }
    }

    val A = sx - sy
    val B = math.sqrt(A * A + 4.0 * pp * pp)
    val C = 2.0 * pp

    val tanTheta = (A + B) / C
    val theta = math.atan(tanTheta)

    val cosT = math.cos(theta)
    val sinT = math.sin(theta)

    var a1 = 0.0
    var a2 = 0.0

    delXY.foreach {
      case (lx, ly) => {
        val d1 = lx * cosT - ly * sinT
        a1 += d1 * d1

        val d2 = lx * sinT + ly * cosT
        a2 += d2 * d2
      }
    }

    val sigmaX = math.sqrt(2.0) * math.sqrt(a1 / n)
    val sigmaY = math.sqrt(2.0) * math.sqrt(a2 / n)

    new StdEllipse(mx, my, theta, sigmaX, sigmaY)
  }

}
