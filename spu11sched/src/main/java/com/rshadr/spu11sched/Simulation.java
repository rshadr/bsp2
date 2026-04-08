/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


public class Simulation
{
  private final Configuration _config;

  private final Scheduler _scheduler;
  private final Distribution _distribution;
  private final int _maxDuration;
  private final int _numProcessors;
  private final Task[] _tasks;
  private final int _numTasks;

  private HashSet<Job> jobs;
  private HashSet<Task> _unstartedTasks;
  private int curTime;

  private final List<Tracker> _trackers;
  private final OutputBackend _outputBackend;

  public static class Tick {
    protected int _tick;

    private
    Tick ()
    {
    }
  }

  public
  Simulation (Configuration.Builder configBuilder, int seed)
  throws IllegalArgumentException
  {
    configBuilder.validate();

    _config = configBuilder.build(this);

    _scheduler = configBuilder.schedulerBuilder.build();
    _distribution = configBuilder.distributionBuilder.build();
    _maxDuration = configBuilder.maxDuration;
    _numProcessors = configBuilder.numProcessors;
    _tasks = configBuilder.tasks;
    _numTasks = configBuilder.numTasks;

    _unstartedTasks = new HashSet<Task>();
    for (int i = 0, j = 0;
         j < _numTasks && i < Configuration.MAX_TASKS;
         ++i) {
      if (_tasks[i] != null) {
        _unstartedTasks.add(_tasks[i]);
      }
    }
    _trackers = configBuilder.trackerBuilders
     .stream()
     .map(builder -> builder.build())
     .collect(Collectors.toUnmodifiableList());
    _outputBackend = configBuilder.outputBackendBuilder.build();
  }


  private void
  runTick_ ()
  {
  }


  public void
  run ()
  {
    for (; curTime < _maxDuration; ++curTime) {
    }
  }


  public void
  output ()
  {
/*
    Object[] trackerRecords = new Object();
    _outputBackend.outputRecord(trackerRecords);
*/
  }


  /*
   * Proxy methods for Configuration
   */
  protected int
  getMaxDuration ()
  {
    return _maxDuration;
  }

  protected int
  getNumProcessors ()
  {
    return _numProcessors;
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

