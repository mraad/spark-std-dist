package com.esri

case class OnlineVar(var n: Int = 0, var mu: Double = 0.0, var m2: Double = 0.0) {

  def +=(x: Double) = {
    n += 1
    val delta = x - mu
    mu += delta / n
    m2 += delta * (x - mu)
    this
  }

  def variance() = m2 / n

  def varianceSample() = m2 / (n - 1)
}
