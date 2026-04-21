/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched.Distributions;
import com.rshadr.spu11sched.Distribution;


public final class Poisson implements Distribution {
  private final int _lambda;

  private
  Poisson (int lambda)
  {
    _lambda = lambda;
  }


  private double
  inverseFactorial_ (int n)
  {
    double acc = 1.0;

    for (; n > 0; --n) {
      acc *= (1.0 / (double)(n));
    }

    return acc;
  }


  public double
  restartProbability (int forwardDelta)
  {
    int k = forwardDelta;

    return (Math.pow(_lambda, k) * Math.exp(-k) * inverseFactorial_(k));
  }


  public static class Builder {
    private int _lambda;

    public
    Builder ()
    {
      _lambda = 1;
    }


    public Builder
    lambda (int lambda)
    throws IllegalArgumentException
    {
      if (lambda < 1) {
        throw new IllegalArgumentException("lambda must be at least 1");
      }

      _lambda = lambda;
      return this;
    }


    public Distribution
    build ()
    {
      return new Poisson(_lambda);
    }
  }
}

