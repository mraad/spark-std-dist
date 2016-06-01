package com.esri

import org.scalatest._
import resource._

import scala.io.Source

/**
  */
class StdDistSpec extends FlatSpec with Matchers {

  val eps = 1.0E-6

  it should "match points with std-dist" in {
    for (sourcePoints <- managed(Source.fromURL(getClass.getResource("/points.csv")))) {
      val empMap = Map[String, Seq[(Double, Double)]]().withDefaultValue(Seq.empty[(Double, Double)])
      val gidMap = sourcePoints
        .getLines
        .drop(1)
        .map(line => {
          val tokens = line.split(',')
          (tokens(1), (tokens(4).toDouble, tokens(5).toDouble))
        })
        .foldLeft(empMap) {
          case (prev, (key, xy)) => {
            prev.updated(key, prev(key) :+ xy)
          }
        }
      for (dirDistSource <- managed(Source.fromURL(getClass.getResource("/std-dist.csv")))) {
        dirDistSource
          .getLines
          .drop(1)
          .foreach(line => {
            val t = line.split(',')
            val mx = t(1).toDouble
            val my = t(2).toDouble
            val sd = t(3).toDouble
            val gid = t(4)
            gidMap.get(gid) match {
              case Some(iter) => {
                val stdDist = StdDist(iter)

                stdDist.mx shouldBe (mx +- eps)
                stdDist.my shouldBe (my +- eps)

                stdDist.sd shouldBe (sd +- eps)
              }
              case _ => fail(s"Cannot find std-dist entry for $gid")
            }
          })
      }
    }
  }
}
