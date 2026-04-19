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

  private final DistributionSampler _distributionSampler;
  private final PriorityQueue<SporadicReoccurence> _reoccuringTasks;

  private int _curTime;

  private final List<Tracker> _trackers;
  private final OutputBackend _outputBackend;

  private
  Simulation (Configuration.Builder configBuilder, int seed)
  throws IllegalArgumentException
  {
    configBuilder.validate();
    _config = configBuilder.build(this);

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
    _distribution = configBuilder.distributionBuilder.build();

    /*
     * Sporadic delays
     */
    _distributionSampler = new DistributionSampler(
     _distribution, _config.getMaxSporadicDelay(), _config.getFreqScale());

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
                     int seed)
  throws IllegalArgumentException
  {
    return new Simulation(configBuilder, seed);
  }


  private void
  runTick_ ()
  throws DeadlineMissedException
  {
    for (Job job : _liveJobs) {
      if (_curTime >= job.getAbsoluteDeadline()) {
        throw new DeadlineMissedException(job, _curTime);
      }
    }

    /*
     * Activate reoccuring jobs
     */
    for (SporadicReoccurence reocc = _reoccuringTasks.peek();
         reocc != null && reocc.restartInstant() <= _curTime;
         reocc = _reoccuringTasks.peek()) {
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

    for (_curTime = 0; _curTime < _config.getMaxDuration(); ++_curTime) {
      runTick_();
    }
  }


  public void
  output ()
  throws java.io.IOException
  {
    List<Tracker.Data> trackerDatas = _trackers
     .stream()
     .map((tracker) -> tracker.onFinish())
     .collect(Collectors.toUnmodifiableList());

    _outputBackend.outputTrackerDatas(trackerDatas);
  }


  protected Scheduler
  getScheduler ()
  {
    return _scheduler;
  }

  protected Distribution
  getDistribution ()
  {
    return _distribution;
  }
}

