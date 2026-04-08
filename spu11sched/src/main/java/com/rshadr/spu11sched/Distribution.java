package com.rshadr.spu11sched;


public interface Distribution {
  public double restartProbability (int forwardDelta);

  public static interface Builder {
    public Distribution build();
  }
};

