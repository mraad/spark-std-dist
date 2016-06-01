package com.esri

import org.scalatest._
import resource._

import scala.io.Source

/**
  */
class DirDistSpec extends FlatSpec with Matchers {

  val eps = 1.0E-6

  it should "match points with dir-dist" in {
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
      for (dirDistSource <- managed(Source.fromURL(getClass.getResource("/dir-dist.csv")))) {
        dirDistSource
          .getLines
          .drop(1)
          .foreach(line => {
            val t = line.split(',')
            val mx = t(1).toDouble
            val my = t(2).toDouble
            val sx = t(3).toDouble
            val sy = t(4).toDouble
            val deg = t(5).toDouble
            val gid = t(6)
            gidMap.get(gid) match {
              case Some(iter) => {
                val dirDist = DirDist(iter)

                dirDist.mx shouldBe (mx +- eps)
                dirDist.my shouldBe (my +- eps)

                dirDist.degrees shouldBe (deg +- eps)

                dirDist.sx shouldBe (sx +- eps)
                dirDist.sy shouldBe (sy +- eps)

              }
              case _ => fail(s"Cannot find dir-dist entry for $gid")
            }
          })
      }
    }
  }
}
