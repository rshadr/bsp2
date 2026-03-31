/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;


public class Job
{
  protected final Task mTask;
  protected final int mOffset;
  protected int mRemaining;
  protected final int mAbsoluteDeadline;
  protected boolean mIsOnline;


  /*
   * Initial job
   */
  protected
  Job (Task task)
  {
    mTask = task;

    mOffset = task.mInitialOffset;
    mRemaining = task.mWcet;
    mAbsoluteDeadline = mOffset + task.mRelativeDeadline;

    mIsOnline = false;
  }


  /*
   * Sporadic reoccurences
   */
  protected
  Job (Job previousJob, int curTime)
  {
    mTask = previousJob.mTask;

    mOffset = curTime;
    mRemaining = mTask.mWcet;
    mAbsoluteDeadline = mOffset + mTask.mRelativeDeadline;
    mIsOnline = false;
  }


  public int
  getOffset ()
  {
    return mOffset;
  }


  public int
  getRemaining ()
  {
    return mRemaining;
  }


  public int
  getAbsoluteDeadline ()
  {
    return mAbsoluteDeadline;
  }
}

