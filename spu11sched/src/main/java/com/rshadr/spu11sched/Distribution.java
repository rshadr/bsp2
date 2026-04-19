/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;


public interface Distribution {
  public double restartProbability (int forwardDelta);

  public static interface Builder {
    public Distribution build();
  }
};

