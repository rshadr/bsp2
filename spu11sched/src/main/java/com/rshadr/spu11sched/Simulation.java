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
import java.util.logging.Logger;
import java.util.logging.Level;


public class Simulation
{
  private static final Logger LOGGER =
   Logger.getLogger(Simulation.class.getName());

  private static final record SporadicReoccurence(
   Task task, int restartInstant){}

  public static enum Result {
    SUCCESS,
    DEADLINE_MISSED
  }


  private final Configuration _config;

  private final Scheduler _scheduler;
  private final Distribution _distribution;

  private final ProcessorGroup _processorGroup;

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


    if (configBuilder.numProcessors <= 0) {
      throw new IllegalArgumentException("Number of processors should be > 0");
    }
    _processorGroup = new ProcessorGroup(configBuilder.numProcessors);

    _finishedJobs = new ArrayList<Job>();
    _liveJobs = new HashSet<Job>();


    _scheduler = configBuilder.schedulerBuilder.build(_processorGroup);
    _distribution = configBuilder.distribution;

    _processorGroup.setScheduler(_scheduler);

    /*
     * Sporadic delays
     */
    _distributionSampler = new DistributionSampler(
     _distribution, _config.getMaxSporadicDelay(), seed);

    _reoccuringTasks = new PriorityQueue(
     Comparator.comparing(SporadicReoccurence::restartInstant));
    for (Task task : _config.getTasks()) {
      _reoccuringTasks.add(new SporadicReoccurence(task, task.initialStartTime()));
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

    _processorGroup.startTick(_curTime);

    for (SporadicReoccurence reocc = _reoccuringTasks.peek();
         reocc != null && reocc.restartInstant() <= _curTime;
         reocc = _reoccuringTasks.peek()) {
      /*
       * Activate job
       */
      Task task = reocc.task();
      Job job = Job.forTaskAndTime(task, _curTime);
      _liveJobs.add(job);
      LOGGER.log(Level.FINER,
       _curTime+" :: Job "+job.getPriority()+" activated");
      _scheduler.onActivate(job);

      for (Tracker tracker : _trackers) {
        tracker.onJobActivated(_curTime, job);
      }

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
      LOGGER.log(Level.FINER,
       _curTime+" :: Restart instant: "+restartInstant);
    }

    for (Processor proc : _processorGroup.getList()) {
      proc.tickStep().ifPresent(endedJob -> _finishedJobs.add(endedJob));
    }

    for (Job job : _finishedJobs) {
      LOGGER.log(Level.FINER,
       _curTime+" :: Terminated: "+job.getPriority());
      for (Tracker tracker : _trackers) {
        tracker.onJobTerminated(_curTime, job);
      }
      _liveJobs.remove(job);
      _scheduler.onTerminate(job);
    }
    _finishedJobs.clear();

    List<Scheduler.Decision> decisions = _processorGroup.endTick(_curTime);

    LOGGER.log(Level.FINER,
     _curTime+" :: total decisions made: "+decisions.size());
    for (Tracker tracker : _trackers) {
      tracker.onSchedule(_curTime, decisions);
    }

  }


  public Result
  run ()
  {
    _trackers.forEach(t -> t.onInitialize(_config));

    try {
      for (_curTime = 0; _curTime < _config.getMaxDuration(); ++_curTime) {
        runTick_();
      }
      _trackerDatas = _trackers
       .stream()
       .map((tracker) -> tracker.onFinish(_curTime, Result.SUCCESS))
       .collect(Collectors.toUnmodifiableList());
    } catch (DeadlineMissedException e) {
      _trackerDatas = _trackers
       .stream()
       .map((tracker) -> tracker.onFinish(_curTime, Result.DEADLINE_MISSED))
       .collect(Collectors.toUnmodifiableList());

      return Result.DEADLINE_MISSED;
    }

    return Result.SUCCESS;
  }


  public void
  output ()
  throws java.io.IOException
  {
    _outputBackend.outputTrackerDatas(_trackerDatas);
  }

}

