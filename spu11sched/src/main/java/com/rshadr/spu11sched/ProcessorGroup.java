/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.lang.ref.WeakReference;
import java.util.logging.Logger;
import java.util.logging.Level;


public class ProcessorGroup
{
  private static final Logger LOGGER =
   Logger.getLogger(ProcessorGroup.class.getName());

  private final List<Processor> _processors;

  /* XXX: don't need weakref*/
  private WeakReference<Scheduler> _scheduler; /* break ref cycle */
  private List<Tracker> _trackers;

  private List<Scheduler.Decision> _tickDecisions = new ArrayList<Scheduler.Decision>();
  private int _curTime = 0;

  protected
  ProcessorGroup (int numProcessors)
  {
    ArrayList<Processor> procs = new ArrayList<Processor>(numProcessors);
    for (int i = 0; i < numProcessors; ++i) {
      procs.add(new Processor(i));
    }

    _processors = Collections.unmodifiableList(procs);
  }


  protected void
  setScheduler (Scheduler scheduler)
  {
    _scheduler = new WeakReference(scheduler);
  }


  protected void
  setTrackerList (List<Tracker> trackers)
  {
    _trackers = trackers;
  }


  protected void
  startTick (int curTime)
  {
    _curTime = curTime;
    _tickDecisions.clear();
  }


  protected List<Scheduler.Decision>
  endTick (int curTime)
  throws IllegalStateException
  {
    if (curTime != _curTime) {
      throw new IllegalStateException("wrong timestamp");
    }

    return _tickDecisions;
  }


  public List<Processor>
  getList ()
  {
    return _processors;
  }


  public void
  reschedule ()
  {
    Scheduler sched = _scheduler.get();

    List<Scheduler.Decision> decisions = sched.schedule();
    for (Scheduler.Decision d : decisions) {
      /* XXX: log (level debug */
      LOGGER.log(Level.FINER, _curTime+" :: Decision: "+
       d.processor().getId()+";"+d.job().getPriority());
      /* XXX: misleading name "preempt" */
      d.processor().preempt(d.job());

      _tickDecisions.add(d);
    }
  }

}

