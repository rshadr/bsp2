/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched.Distributions;
import com.rshadr.spu11sched.Distribution;


public final class Geometric implements Distribution {
  private double _p;
  private double _q;

  private
  Geometric (double p)
  {
    _p = p;
    _q = 1.0 - p;
  }

  public double
  restartProbability (int forwardDelta)
  {
    return (Math.pow(_q, forwardDelta) * _p);
  }


  public static final class Builder implements Distribution.Builder {
    private double _p;

    public
    Builder ()
    {
      _p = 0.5;
    }

    public Builder
    p (double p)
    throws IllegalArgumentException
    {
      if (p <= 0.0 || p >= 1.0) {
        throw new IllegalArgumentException("p must be in the range ]0;1[");
      }

      _p = p;
      return this;
    }

    public Geometric
    build ()
    {
      return new Geometric(_p);
    }
  }
}

