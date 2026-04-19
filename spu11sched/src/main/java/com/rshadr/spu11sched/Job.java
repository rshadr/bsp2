/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;


public final class Job
{
  private final int _priority;
  protected final Task _task;
  protected final int _startInstant;
  protected int _remainingTime;
  protected final int _absoluteDeadline;
  protected Processor _processor;


  private
  Job (Task task, int startTime)
  {
    _task = task;
    _priority = task.priority();
    _startInstant = startTime;
    _remainingTime = task.wcet();
    _absoluteDeadline = _startInstant + task.relativeDeadline();
    _processor = null;
  }


  protected static Job
  forTaskAndTime (Task task, int startTime)
  {
    return new Job(task, startTime);
  }


  public Task
  getTask ()
  {
    return _task;
  }


  public int
  getPriority ()
  {
    return _priority;
  }


  /*
   * XXX: change name to less ambiguous
   */
  public int
  getStartInstant ()
  {
    return _startInstant;
  }


  public int
  getRemaining ()
  {
    return _remainingTime;
  }


  protected void
  advance (int forwardDelta)
  throws IllegalArgumentException
  {
    if (forwardDelta <= 0) {
      throw new IllegalArgumentException("forwardDelta must be strictly positive");
    }

    if (forwardDelta > _remainingTime) {
      throw new IllegalArgumentException("forwardDelta exceeds remaining time");
    }

    _remainingTime -= forwardDelta;
  }


  public int
  getAbsoluteDeadline ()
  {
    return _absoluteDeadline;
  }


  public Processor
  getProcessor ()
  {
    return _processor;
  }
}

