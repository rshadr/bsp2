/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */

package com.rshadr.spu11sched;

import java.lang.IllegalArgumentException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.ArrayList;

public final class Configuration
{
  public static final int MAX_TASKS = 100;
  public static final int MAX_TRACKERS = 20;

  private final WeakReference<Simulation> _simulation;
  private final int _maxDuration;
  private final int _numProcessors;
  private final int _maxSporadicDelay;
  private final int _freqScale;

  private
  Configuration (Simulation simulation,
                 int maxDuration,
                 int numProcessors,
                 int maxSporadicDelay,
                 int freqScale)
  {
    _simulation = new WeakReference(simulation);
    _maxDuration = maxDuration;
    _numProcessors = numProcessors;
    _maxSporadicDelay = maxSporadicDelay;
    _freqScale = freqScale;
  }


  public int
  getMaxDuration ()
  {
    return _maxDuration;
  }


  public int
  getNumProcessors ()
  {
    return _numProcessors;
  }


  public int
  getMaxSporadicDelay ()
  {
    return _maxSporadicDelay;
  }


  public int
  getFreqScale ()
  {
    return _freqScale;
  }


  public final static class Builder
  {
    protected int maxDuration;
    protected int numProcessors;
    protected int maxSporadicDelay;
    protected int freqScale;
    protected Scheduler.Builder schedulerBuilder;
    /* XXX: use direct instance instead */
    protected Distribution.Builder distributionBuilder;

    protected ArrayList<Task> tasks;

    protected List<Tracker.Builder> trackerBuilders;
    protected OutputBackend.Builder outputBackendBuilder;


    public
    Builder ()
    {
      maxDuration = 100;
      numProcessors = 1;
      maxSporadicDelay = 0;
      freqScale = 1000;
      tasks = new ArrayList<Task>(MAX_TASKS);
      trackerBuilders = new ArrayList<Tracker.Builder>();
    }


    public Builder
    maxDuration (int maxDuration)
    throws IllegalArgumentException
    {
      if (maxDuration <= 0) {
        throw new IllegalArgumentException("Max. duration must be strictly positive");
      }
      this.maxDuration = maxDuration;

      return this;
    }


    public Builder
    numProcessors (int numProcessors)
    throws IllegalArgumentException
    {
      if (numProcessors <= 0) {
        throw new IllegalArgumentException("No° of processors must be strictly positive");
      }
      this.numProcessors = numProcessors;

      return this;
    }


    public Builder
    schedulerBuilder (Scheduler.Builder schedulerBuilder)
    {
      this.schedulerBuilder = schedulerBuilder;
      return this;
    }


    public Builder
    distributionBuilder (Distribution.Builder distributionBuilder)
    {
      this.distributionBuilder = distributionBuilder;
      return this;
    }


    public Builder
    maxSporadicDelay (int maxSporadicDelay)
    throws IllegalArgumentException
    {
      if (maxSporadicDelay < 0) {
        throw new IllegalArgumentException(
         "Maximum sporadic delay must be greater than 0");
      }

      this.maxSporadicDelay = maxSporadicDelay;
      return this;
    }


    public Builder
    freqScale (int freqScale)
    throws IllegalArgumentException
    {
      if (freqScale < 10) {
        throw new IllegalArgumentException(
         "Frequency scale must be at least 10");
      }

      this.freqScale = freqScale;
      return this;
    }


    public Builder
    addTask (Task task)
    throws IllegalArgumentException
    {
      if (tasks.size() == MAX_TASKS) {
        throw new IllegalArgumentException("Excess task");
      }

      if (tasks.stream().anyMatch(t -> t.priority() == task.priority())) {
        throw new IllegalArgumentException(
         "Task with priority "+task.priority()+" existing");
      }
      this.tasks.add(task);
      return this;
    }


    public Builder
    addTrackerBuilder (Tracker.Builder trackerBuilder)
    {
      this.trackerBuilders.add(trackerBuilder);
      return this;
    }


    public Builder
    outputBackendBuilder (OutputBackend.Builder outputBackendBuilder)
    {
      this.outputBackendBuilder = outputBackendBuilder;
      return this;
    }


    protected void
    validate ()
    throws IllegalArgumentException
    {
      if (schedulerBuilder == null) {
        throw new IllegalArgumentException(
         "Scheduler builder required");
      }

      if (distributionBuilder == null) {
        throw new IllegalArgumentException(
         "Distribution builder required");
      }

      if (outputBackendBuilder == null) {
        throw new IllegalArgumentException(
         "Output backend builder required");
      }
    }


    protected Configuration
    build (Simulation simulation)
    {
      Configuration config = new Configuration(
       simulation,
       maxDuration,
       numProcessors,
       maxSporadicDelay,
       freqScale);
      return config;
    }
  }
}

