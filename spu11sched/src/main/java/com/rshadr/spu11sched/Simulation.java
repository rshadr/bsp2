/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.Collections;


public class Simulation
{
  private static final record SporadicReoccurence(
   Task task, int restartInstant){}


  private final Configuration _config;

  private final Scheduler _scheduler;
  private final Distribution _distribution;

  private final List<Task> _tasks;

  private List<Processor> _processors;

  private final ArrayList<Job> _finishedJobs;
  private final HashSet<Job> _liveJobs;
  private List<Tracker.Data> _trackerDatas;

  private final DistributionSampler _distributionSampler;
  private final PriorityQueue<SporadicReoccurence> _reoccuringTasks;

  private int _curTime;

  private final List<Tracker> _trackers;
  private final OutputBackend _outputBackend;

  private
  Simulation (Configuration.Builder configBuilder, long seed)
  throws IllegalArgumentException
  {
    configBuilder.validate();
    _config = configBuilder.build();

    _tasks = Collections.unmodifiableList(configBuilder.tasks);

    do {
      int numProcessors = configBuilder.numProcessors;
      ArrayList<Processor> processors = new ArrayList<Processor>(numProcessors);

      for (int i = 0; i < numProcessors; ++i) {
        processors.add(new Processor(i));
      }

      _processors = Collections.unmodifiableList(processors);
    } while (false);

    _finishedJobs = new ArrayList<Job>();
    _liveJobs = new HashSet<Job>();


    _scheduler = configBuilder.schedulerBuilder.build(_processors);
    _distribution = configBuilder.distribution;

    /*
     * Sporadic delays
     */
    _distributionSampler = new DistributionSampler(
     _distribution, _config.getMaxSporadicDelay(), _config.getFreqScale(), seed);

    _reoccuringTasks = new PriorityQueue(
     Comparator.comparing(SporadicReoccurence::restartInstant));
    for (Task task : _tasks) {
      _reoccuringTasks.add(new SporadicReoccurence(task, task.initialOffset()));
    }

    _trackers = configBuilder.trackerBuilders
     .stream()
     .map(builder -> builder.build())
     .collect(Collectors.toUnmodifiableList());
    _outputBackend = configBuilder.outputBackendBuilder.build();

  }


  static Simulation
  withConfigAndSeed (Configuration.Builder configBuilder,
                     long seed)
  throws IllegalArgumentException
  {
    return new Simulation(configBuilder, seed);
  }


  private void
  runTick_ ()
  throws DeadlineMissedException
  {
    /*
     * Check deadlines first of all
     */
    for (Job job : _liveJobs) {
      if (_curTime >= job.getAbsoluteDeadline()) {
        throw new DeadlineMissedException(job, _curTime);
      }
    }

    for (SporadicReoccurence reocc = _reoccuringTasks.peek();
         reocc != null && reocc.restartInstant() <= _curTime;
         reocc = _reoccuringTasks.peek()) {
      /*
       * Activate job
       */
      Task task = reocc.task();
      Job job = Job.forTaskAndTime(task, _curTime);
      _liveJobs.add(job);
      System.out.println(_curTime + ":: Job "+job.getPriority()+" activated");
      _scheduler.onActivate(job);

      /*
       * Remove this entry
       */
      _reoccuringTasks.poll();

      /*
       * Queue next one
       */
      int restartInstant = job.getStartInstant() + task.minIAT();
      int delay = _distributionSampler.getNext();
      restartInstant += delay;
      _reoccuringTasks.add(new SporadicReoccurence(task, restartInstant));
      System.out.println("restart instant: "+restartInstant);
    }
    

    List<Scheduler.Decision> decisions = _scheduler.schedule();

    for (Scheduler.Decision decision : decisions) {
      System.out.println(_curTime + " :: Decision: "+decision.job().getPriority());
      /* XXX: misleading name "preempt" */
      decision.processor().preempt(decision.job());
    }

    for (Processor proc : _processors) {
      proc.tickStep().ifPresent(endedJob -> _finishedJobs.add(endedJob));
    }

    for (Job job : _finishedJobs) {
      System.out.println(
       _curTime + ":: Finished job! (priority "+job.getPriority()+")");
      _liveJobs.remove(job);
      _scheduler.onTerminate(job);
    }
    _finishedJobs.clear();

  }


  public void
  run ()
  throws DeadlineMissedException
  {
    _trackers.forEach(t -> t.onInitialize(_config));

    try {
      for (_curTime = 0; _curTime < _config.getMaxDuration(); ++_curTime) {
        runTick_();
      }
      _trackerDatas = _trackers
       .stream()
       .map((tracker) -> tracker.onFinish(_curTime, null))
       .collect(Collectors.toUnmodifiableList());
    } catch (DeadlineMissedException e) {
      _trackerDatas = _trackers
       .stream()
       .map((tracker) -> tracker.onFinish(_curTime, e))
       .collect(Collectors.toUnmodifiableList());

      throw e;
    }
  }


  public void
  output ()
  throws java.io.IOException
  {
    _outputBackend.outputTrackerDatas(_trackerDatas);
  }

}

