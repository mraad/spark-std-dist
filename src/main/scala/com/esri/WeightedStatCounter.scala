package com.esri

/**
  * http://en.wikipedia.org/wiki/Algorithms_for_calculating_variance#On-line_algorithm
  */
class WeightedStatCounter(values: TraversableOnce[WeightedValue]) extends Serializable {

  private var sw: Double = 0.0
  private var mu: Double = 0.0
  private var m2: Double = 0.0
  private var n: Long = 0

  merge(values)

  def this() = this(Nil)

  def merge(values: TraversableOnce[WeightedValue]): WeightedStatCounter = {
    values.foreach(v => merge(v))
    this
  }

  def merge(weightedValue: WeightedValue): WeightedStatCounter = {
    val t = weightedValue.weight + sw
    val delta = weightedValue.value - mu
    val dw = delta * weightedValue.weight
    mu += dw / t
    m2 += dw * (weightedValue.value - mu)
    sw = t
    n += 1
    this
  }

  def merge(that: WeightedStatCounter): WeightedStatCounter = {
    if (this == that) {
      merge(that.copy())
    }
    else {
      if (n == 0) {
        sw = that.sw
        mu = that.mu
        m2 = that.m2
        n = that.n
      } else if (that.n != 0) {
        val delta = that.mu - mu
        val sumw = that.sw + sw
        mu = (mu * sw + that.mu * that.sw) / sumw
        m2 += that.m2 + (delta * delta * sw * that.sw) / sumw
        sw = sumw
        n += that.n
      }
      this
    }
  }

  def variance(): Double = {
    if (sw != 0.0) {
      m2 / sw
    }
    else {
      Double.NaN
    }
  }

  def copy(): WeightedStatCounter = {
    val that = new WeightedStatCounter()
    that.n = this.n
    that.mu = this.mu
    that.m2 = this.m2
    that.sw = this.sw
    that
  }

  def count(): Long = n

  def mean(): Double = mu

  def stdev(): Double = math.sqrt(variance)

  override def toString(): String = "{count: %d, mean: %f, stdev: %f}".format(count, mean, stdev)
}

object WeightedStatCounter {
  def apply(values: TraversableOnce[WeightedValue]): WeightedStatCounter = new WeightedStatCounter(values)

  def apply(values: WeightedValue*): WeightedStatCounter = new WeightedStatCounter(values)
}