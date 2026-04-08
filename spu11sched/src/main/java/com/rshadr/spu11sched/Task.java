/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;


public class Task {
  protected final int mPriority;
  protected final int mWcet;
  protected final int mInitialOffset;
  protected final int mRelativeDeadline;
  protected final int mMinIAT;

  public
  Task (int priority,
        int wcet,
        int initialOffset,
        int relativeDeadline,
        int minimumInterArrivalTime)
  throws IllegalArgumentException
  {
    if (priority < 0 || priority >= Configuration.MAX_TASKS) {
      throw new IllegalArgumentException(
       "Task priority must be between 0 and "+(Configuration.MAX_TASKS - 1));
    }

    if (initialOffset < 0) {
      throw new IllegalArgumentException("Initial offset must be 0 or greater");
    }

    if (wcet < 1) {
      throw new IllegalArgumentException("WCET must be at least 1");
    }

    if (relativeDeadline <= 0 || relativeDeadline < wcet) {
      throw new IllegalArgumentException("Relative deadline cannot be lower than WCET");
    }

    if (minimumInterArrivalTime <= 0 || minimumInterArrivalTime < relativeDeadline) {
      throw new IllegalArgumentException(
       "Minimum inter-arrival time must be at least deadline");
    }

    mPriority = priority;
    mWcet = wcet;
    mInitialOffset = initialOffset;
    mRelativeDeadline = relativeDeadline;
    mMinIAT = minimumInterArrivalTime;
  }


  public int
  getPriority ()
  {
    return mPriority;
  }


  public int
  getWcet ()
  {
    return mWcet;
  }


  public int
  getInitialOffset ()
  {
    return mInitialOffset;
  }


  public int
  getRelativeDeadline ()
  {
    return mRelativeDeadline;
  }


  public int
  getMinIAT ()
  {
    return mMinIAT;
  }
}
