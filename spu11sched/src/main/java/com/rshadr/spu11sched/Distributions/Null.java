/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched.Distributions;
import com.rshadr.spu11sched.Distribution;


public final class Null implements Distribution {
  public
  Null ()
  {
  }

  public double
  restartProbability (int forwardDelta)
  {
    return forwardDelta == 0 ? 1.0 : 0.0;
  }
}

