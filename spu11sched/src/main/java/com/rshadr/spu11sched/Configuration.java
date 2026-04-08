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

  private
  Configuration (Simulation simulation)
  {
    this._simulation = new WeakReference<Simulation>(simulation);
  }


  public int
  getMaxDuration ()
  {
    return this._simulation.get().getMaxDuration();
  }


  public int
  getNumProcessors ()
  {
    return this._simulation.get().getNumProcessors();
  }


  public final static class Builder
  {
    protected int maxDuration;
    protected int numProcessors;
    protected Scheduler.Builder schedulerBuilder;
    protected Distribution.Builder distributionBuilder;
    protected Task[] tasks;
    protected int numTasks;
    protected List<Tracker.Builder> trackerBuilders;
    protected OutputBackend.Builder outputBackendBuilder;


    public
    Builder ()
    {
      maxDuration = 100;
      numProcessors = 1;
      tasks = new Task[MAX_TASKS];
      numTasks = 0;
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
    addTask (Task task)
    throws IllegalArgumentException
    {
      if (this.numTasks == MAX_TASKS) {
        throw new IllegalArgumentException("Excess task");
      }

      if (this.tasks[task.mPriority] != null) {
        throw new IllegalArgumentException(
         "Task with priority "+task.mPriority+" existing");
      }
      this.tasks[task.mPriority] = task;
      this.numTasks += 1;
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
      Configuration config = new Configuration(simulation);
      return config;
    }
  }
}

