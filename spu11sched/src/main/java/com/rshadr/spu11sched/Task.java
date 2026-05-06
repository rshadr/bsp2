/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;


public record Task(
 int priority,
 int wcet,
 int initialStartTime,
 int relativeDeadline,
 int minIAT) {
  public
  Task {
    if (priority < 0 || priority >= Configuration.MAX_TASKS) {
      throw new IllegalArgumentException(
       "Task priority must be between 0 and "+(Configuration.MAX_TASKS - 1));
    }

    if (initialStartTime < 0) {
      throw new IllegalArgumentException("Initial offset must be 0 or greater");
    }

    if (wcet < 1) {
      throw new IllegalArgumentException("WCET must be at least 1");
    }

    if (relativeDeadline <= 0 || relativeDeadline < wcet) {
      throw new IllegalArgumentException("Relative deadline cannot be lower than WCET");
    }

    if (minIAT <= 0 || minIAT < relativeDeadline) {
      throw new IllegalArgumentException(
       "Minimum inter-arrival time must be at least deadline");
    }
  }


  static public final class Builder {
   private int _priority;
   private int _wcet;
   private int _initialStartTime;
   private int _relativeDeadline;
   private int _minIAT;

    public
    Builder ()
    {
      _priority = -1;
      _wcet = -1;
      _initialStartTime = -1;
      _relativeDeadline = -1;
      _minIAT = -1;
    }

    public Builder
    priority (int priority)
    {
      _priority = priority;
      return this;
    }

    public Builder
    wcet (int wcet)
    {
      _wcet = wcet;
      return this;
    }

    public Builder
    initialStartTime (int initialStartTime)
    {
      _initialStartTime = initialStartTime;
      return this;
    }

    public Builder
    relativeDeadline (int relativeDeadline)
    {
      _relativeDeadline = relativeDeadline;
      return this;
    }

    public Builder
    minIAT (int minIAT)
    {
      _minIAT = minIAT;
      return this;
    }

    public Task
    build ()
    throws IllegalArgumentException
    {
      return new Task(
       _priority,
       _wcet,
       _initialStartTime,
       _relativeDeadline,
       _minIAT
      );
    }
  }
}

