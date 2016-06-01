package com.esri

/**
  * https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#On-line_algorithm
  */
class MeanVariance(var k: Double, var n: Int = 1, var ex0: Double = 0.0, var ex2: Double = 0.0) extends Serializable {
  def +=(x: Double) = {
    val dx = x - k
    n += 1
    ex0 += dx
    ex2 += dx * dx
    this
  }

  def mean() = {
    k + ex0 / n
  }

  def varianceSample() = {
    (ex2 - (ex0 * ex0) / n) / (n - 1)
  }

  def variancePopulation() = {
    (ex2 - (ex0 * ex0) / n) / n
  }
}

object MeanVariance extends Serializable {
  def apply(x: Double) = {
    new MeanVariance(x)
  }
}