package com.esri

import breeze.linalg.DenseMatrix
import nak.cluster.{DBSCAN, GDBSCAN, Kmeans}

import scala.collection.mutable.ArrayBuffer
import scala.math._

/**
  */
object ScanDist {
  def apply(
             iter: Iterable[(Double, Double)],
             epsilon: Double,
             minPoints: Double
           ): Seq[StdDist] = {

    val (xarr, yarr) = iter
      .foldLeft((new ArrayBuffer[Double](), new ArrayBuffer[Double]())) {
        case ((xarr, yarr), (x, y)) => {
          xarr += x
          yarr += y
          (xarr, yarr)
        }
      }

    val matrix = new DenseMatrix[Double](xarr.size, 2, (xarr ++ yarr).toArray)

    val dbScan = new GDBSCAN(
      DBSCAN.getNeighbours(epsilon = epsilon,
        distance = Kmeans.euclideanDistance),
      DBSCAN.isCorePoint(minPoints = minPoints))

    val clusters = dbScan cluster matrix
    clusters.map(cluster => {
      val (ox, oy) = cluster.points.foldLeft((OnlineVar(), OnlineVar())) {
        case ((ox, oy), point) => {
          val denseVector = point.value
          ox += denseVector(0)
          oy += denseVector(1)
          (ox, oy)
        }
      }
      val sd = sqrt(ox.variance + oy.variance)
      StdDist(ox.mu, oy.mu, sd)
    })
  }

}
