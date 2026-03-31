/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched.Distributions;
import com.rshadr.spu11sched.Distribution;


public final class Poisson implements Distribution {
  private int mLambda_;

  public
  Poisson (int lambda)
  throws IllegalArgumentException
  {
    if (lambda <= 0) {
      throw new IllegalArgumentException("Lambda must be strictly positive");
    }

    mLambda_ = lambda;
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

    return (Math.pow(mLambda_, k) * Math.exp(-k) * inverseFactorial_(k));
  }
}

