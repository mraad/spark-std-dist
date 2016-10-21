package com.esri

import breeze.linalg.DenseMatrix
import nak.cluster.{DBSCAN, GDBSCAN, Kmeans}

import scala.collection.mutable.ArrayBuffer

/**
  */
object ScanDir {
  def apply(
             iter: Iterable[(Double, Double)],
             epsilon: Double,
             minPoints: Int
           ) = {

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
    clusters.flatMap(cluster => {
      val points = cluster
        .points
        .map(point => {
          val denseVector = point.value
          (denseVector(0), denseVector(1))
        })
      DirDist(points, minPoints)
    })
  }
}
