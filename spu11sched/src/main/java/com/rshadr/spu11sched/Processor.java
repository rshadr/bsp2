/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;
import java.lang.ref.WeakReference;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.stream.Collectors;
import java.util.Optional;


public class Processor {
  private Job _runningJob;
  private final int _id;

  protected
  Processor (int id)
  {
    _runningJob = null;
    _id = id;
  }


  public Boolean
  hasRunningJob ()
  {
    return _runningJob != null;
  }


  public Optional<Job>
  getRunningJob ()
  {
    if (_runningJob == null) {
      return Optional.empty();
    }

    return Optional.of(_runningJob);
  }


  public int
  getId ()
  {
    return _id;
  }


  protected void
  preempt (Job job)
  {
    _runningJob = job;
  }


  protected Optional<Job>
  tickStep ()
  {
    if (!hasRunningJob()) {
      return Optional.empty();
    }

    _runningJob.advance(1);

    if (_runningJob.getRemaining() == 0) {
      Optional<Job> finishedJob = Optional.of(_runningJob);
      _runningJob = null;
      return finishedJob;
    }

    return Optional.empty();
  }

}

