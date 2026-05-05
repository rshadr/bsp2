/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched.Schedulers;

import com.rshadr.spu11sched.*;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public final class FixedPriority extends Scheduler {
  private PriorityQueue<Job> _readyQueue;
  private ProcessorGroup _processorGroup;
  //private final int[] _usedProcessors;
  private ProcessorComparator _cmp = new ProcessorComparator();


  /*
   * XXX might get rid of this?
   * Sorting every tick is not particulary efficient.
   */
  private static class ProcessorComparator implements Comparator<Processor> {
    public int
    compare (Processor a, Processor b) {

      if (!a.hasRunningJob()) {
        return -1;
      }

      if (!b.hasRunningJob()) {
        return +1;
      }

      return -Integer.compare(
       a.getRunningJob().get().getPriority(),
       b.getRunningJob().get().getPriority());
    }
  }


  private
  FixedPriority (ProcessorGroup processors)
  {
    _readyQueue = new PriorityQueue<Job>(
     Comparator.comparing(Job::getPriority).reversed());
    _processorGroup = processors;
  }


  public void
  onActivate (Job job)
  {
    _readyQueue.add(job);
    _processorGroup.reschedule();
  }


  public void
  onPreempt (Processor proc, Job preemptedJob)
  {
    /*
     * XXX: why do we even have this
     */
  }


  public void
  onTerminate (Job job)
  {
    _processorGroup.reschedule();
  }


  public List<Decision>
  schedule ()
  {
    /*
     * Scheduling happens every time a job is activated or terminated
     */
    ArrayList<Decision> decisions = new ArrayList<Decision>();

    Processor p = Collections.min(_processorGroup.getList(), _cmp);

    Job pendingJob = _readyQueue.peek();
    if (pendingJob == null) {
      return List.of();
    }

    if (!p.hasRunningJob()
     || p.getRunningJob().get().getPriority() > pendingJob.getPriority()) {
      decisions.add(new Decision(p, pendingJob));
      _readyQueue.remove(pendingJob);

      if (p.hasRunningJob()) {
        _readyQueue.add(p.getRunningJob().get());
      }
    }

    return Collections.unmodifiableList(decisions);
  }


  public static final class Builder implements Scheduler.Builder {
    public
    Builder ()
    {
    }


    public FixedPriority
    build (ProcessorGroup processorGroup)
    {
      FixedPriority fp = new FixedPriority(processorGroup);
      return fp;
    }
  }
}

