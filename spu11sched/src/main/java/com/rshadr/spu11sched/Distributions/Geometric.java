/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched.Distributions;
import com.rshadr.spu11sched.Distribution;


public final class Geometric implements Distribution {
  private double mP_;
  private double mQ_;

  public
  Geometric (double p)
  throws IllegalArgumentException
  {
    if (p <= 0.0 || p >= 1.0) {
      throw new IllegalArgumentException("Bad p value");
    }

    mP_ = p;
    mQ_ = 1.0 - p;
  }

  public double
  restartProbability (int forwardDelta)
  {
    return (Math.pow(mQ_, forwardDelta) * mP_);
  }
}

