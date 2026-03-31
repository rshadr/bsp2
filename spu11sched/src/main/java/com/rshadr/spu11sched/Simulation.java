/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

import java.util.HashSet;


public class Simulation
{
  private final Configuration config;
  private HashSet<Job> jobs;
  private int curTime;

  public
  Simulation (Configuration config)
  {
    this.config = config;
    jobs = new HashSet<Job>(config.mNumTasks);
    curTime = 0;
  }


  private void
  runPrelude_ ()
  {
    for (int i = 0, j = 0;
         j < config.mNumTasks && i < Configuration.MAX_TASKS;
         ++i) {
      if (config.mTasks[i] != null) {
        jobs.add(new Job(config.mTasks[i]));
        ++j;
      }
    }
  }


  public void
  run ()
  {
    runPrelude_();

    for (; curTime < config.mMaxDuration; ++curTime) {
    }
  }


  public void
  output ()
  {
  }
}

