/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */

package com.rshadr.spu11sched;

import java.lang.IllegalAccessException;
import java.util.ArrayList;

public class Configuration
{
  public static final int MAX_TASKS = 100;

  protected int mMaxDuration;
  protected int mNumProcessors;
  protected Distribution mDistribution;
  protected Task[] mTasks;
  protected int mNumTasks;
  protected boolean mIsUsed;

  public
  Configuration ()
  {
    mMaxDuration = -1;
    mNumProcessors = -1;
    mDistribution = null;
    mTasks = new Task[MAX_TASKS];
    mNumTasks = 0;
  }


  private void
  maybeInUse_()
  throws IllegalAccessException
  {
    if (mIsUsed) {
      throw new IllegalAccessException("Configuration in use");
    }
  }


  public void
  setMaxDuration (int maxDuration)
  throws IllegalAccessException
  {
    maybeInUse_();
    mMaxDuration = maxDuration;
  }

  public void
  setNumProcessors (int numProcessors)
  throws IllegalAccessException
  {
    maybeInUse_();

    assert numProcessors == 1
     : "Only one processor is supported right now";
    mNumProcessors = numProcessors;
  }


  public void
  setDistribution (Distribution distribution)
  throws IllegalAccessException
  {
    maybeInUse_();

    mDistribution = distribution;
  }


  public void
  addTask (Task task)
  throws IllegalAccessException
  {
    maybeInUse_();

    assert mTasks[task.mPriority] == null
     : "Task with priority "+task.mPriority+" existing";

    mTasks[task.mPriority] = task;
    mNumTasks += 1;
  }

}

