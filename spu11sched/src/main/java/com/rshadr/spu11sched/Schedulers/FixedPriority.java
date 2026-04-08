/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched.Schedulers;

import com.rshadr.spu11sched.*;
import java.util.HashSet;

public final class FixedPriority implements Scheduler {
  private HashSet<Job> readyList;

  private
  FixedPriority ()
  {
    readyList = new HashSet<Job>(67);
  }


  public void
  onActivate (Job job)
  {
  }


  public void
  onTerminate (Job job)
  {
  }


  public void
  schedule ()
  {
  }


  public static final class Builder implements Scheduler.Builder {
    public
    Builder ()
    {
    }


    public FixedPriority
    build ()
    {
      FixedPriority fp = new FixedPriority();
      return fp;
    }
  }
}

