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
  private PriorityQueue<Processor> _processorQueue;


  /*
   * XXX might get rid of this?
   * Sorting every tick is not particulary efficient.
   */
  private static class ProcessorComparator implements Comparator<Processor> {
    public int
    compare (Processor a, Processor b) {
      int keys[][] = {
        { a.hasRunningJob() ? 1 : 0, a.getRunningJob().get().getPriority() },
        { b.hasRunningJob() ? 1 : 0, b.getRunningJob().get().getPriority() },
      };

      if (keys[0][0] != keys[1][0]) {
        return Integer.compare(keys[0][0], keys[1][0]);
      }

      return -Integer.compare(keys[0][1], keys[1][1]);
    }
  }


  private
  FixedPriority (List<Processor> processors)
  {
    _readyQueue = new PriorityQueue<Job>(Comparator.comparing(Job::getPriority));

    _processorQueue = new PriorityQueue<Processor>(new ProcessorComparator());
    processors.forEach(p -> _processorQueue.add(p));
  }


  public void
  onActivate (Job job)
  {
    _readyQueue.add(job);
  }


  public void
  onPreempt (Processor proc, Job preemptedJob)
  {
    
  }


  public void
  onTerminate (Job job)
  {
  }


  public List<Decision>
  schedule ()
  {
    ArrayList<Decision> decisions = new ArrayList<Decision>();

    /*
     * XXX: queue is not updated live!!! works for 1 core though
     */
    for (int i = 0;
         _readyQueue.size() > 0 && i < _processorQueue.size();
         ++i) {
      Processor p = _processorQueue.peek();
      Job pendingJob = _readyQueue.peek();

      if (!p.hasRunningJob()
       || p.getRunningJob().get().getPriority() > pendingJob.getPriority()) {
        decisions.add(new Decision(p, pendingJob));
        _readyQueue.remove(pendingJob);

        if (p.hasRunningJob()) {
          _readyQueue.add(p.getRunningJob().get());
        }
      } else { break; }
    }

    return Collections.unmodifiableList(decisions);
  }


  public static final class Builder implements Scheduler.Builder {
    public
    Builder ()
    {
    }


    public FixedPriority
    build (List<Processor> processors)
    {
      FixedPriority fp = new FixedPriority(processors);
      return fp;
    }
  }
}

