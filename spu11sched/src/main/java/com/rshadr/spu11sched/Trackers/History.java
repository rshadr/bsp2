/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */

package com.rshadr.spu11sched;

public final class History implements Tracker
{
  private int _maxDuration;
  private int _numProcessors;

  private
  History ()
  {
    _maxDuration = -1;
    _numProcessors = -1;
  }


  public void
  onInitialize (Configuration config)
  {
    _maxDuration = config.getMaxDuration();
    _numProcessors = config.getNumProcessors();
  }


  public void
  onActiveTick ()
  {
  }


  public static final class Builder implements Tracker.Builder {
    public
    Builder ()
    {
    }

    public History
    build ()
    {
      return new History();
    }
  }
}

