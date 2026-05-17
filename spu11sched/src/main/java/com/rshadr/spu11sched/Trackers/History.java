/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

public final class History extends Tracker
{
  static private final Logger LOGGER =
   Logger.getLogger(History.class.getName());

  static private final record Config(
   int maxDuration,
   int numProcessors,
   /* XXX: distribution */
   int maxSporadicDelay,
   Task[] tasks) {}

  static private final record TrackerOptions(
    Boolean coerceSameTickDecisions
  ) {}

  static private final record Decision(
    int processor,
    int job) {}

  static private final record Tick(
    int t,
    int[] activatedJobs,
    int[] terminatedJobs,
    Decision[] decisions) {}

  static private final record Result(
    int finishTick,
    Simulation.Result result) {}

  private Config _config;
  private TrackerOptions _trackerOptions;
  private int _curTime;
  private final List<Tick> _ticks = new ArrayList<Tick>();
  private final List<Job> _activatedJobs = new ArrayList<Job>();
  private final List<Job> _terminatedJobs = new ArrayList<Job>();

  private final List<Decision> _filteredDecisions = 
   new ArrayList<Decision>();
  private Decision[] _processorDecisionMap;

  private Result _result;

  private
  History (Boolean coerceSameTickDecisions)
  {
    _config = null;
    _trackerOptions = new TrackerOptions(coerceSameTickDecisions);
  }


  public void
  onInitialize (Configuration config)
  {
    Task[] tasks = new Task[config.getTasks().size()];
    config.getTasks().toArray(tasks);

    _config = new Config(
     config.getMaxDuration(),
     config.getNumProcessors(),
     config.getMaxSporadicDelay(),
     tasks);

    _processorDecisionMap = new Decision[config.getNumProcessors()];
  }


  private void
  closeTick ()
  {
    if (_trackerOptions.coerceSameTickDecisions()) {
      for (int i = 0; i < _processorDecisionMap.length; ++i) {
        Decision dec = _processorDecisionMap[i];
        if (dec != null) {
          _filteredDecisions.add(dec);
        }
      }
    }

    Decision[] flatDecisions = new Decision[_filteredDecisions.size()];

    _ticks.add(new Tick(
      _curTime,
      _activatedJobs.size() > 0
       ? _activatedJobs.stream().mapToInt(job -> job.getPriority()).toArray()
       : null,
      _terminatedJobs.size() > 0
       ? _terminatedJobs.stream().mapToInt(job -> job.getPriority()).toArray()
       : null,
      _filteredDecisions.size() > 0
       ? _filteredDecisions.toArray(flatDecisions)
       : null));

    _activatedJobs.clear();
    _terminatedJobs.clear();
    _filteredDecisions.clear();

    for (int i = 0; i < _processorDecisionMap.length; ++i) {
      _processorDecisionMap[i] = null;
    }
  }


  private void
  maybeCloseTick (int t)
  {
    if (t > _curTime) {
      closeTick();
      _curTime = t;
    }
  }


  public void
  onJobActivated (int t, Job job)
  {
    maybeCloseTick(t);

    _activatedJobs.add(job);
  }


  public void
  onJobTerminated (int t, Job job)
  {
    maybeCloseTick(t);

    _terminatedJobs.add(job);
  }


  public void
  onSchedule (int t, List<Scheduler.Decision> decisions)
  {
    maybeCloseTick(t);

    if (decisions.size() == 0) {
      return;
    }

    for (Scheduler.Decision d : decisions) {
      Decision dec = new Decision(
       d.processor().getId(),
       d.job().getPriority());
      LOGGER.log(Level.FINE, "History schedule on tick "+t+
       dec.processor()+":"+dec.job());

      if (_trackerOptions.coerceSameTickDecisions()) {
        _processorDecisionMap[dec.processor()] = dec;
      } else {
        _filteredDecisions.add(dec);
      }
    }
  }


  public Data
  onFinish (int t, Simulation.Result result)
  {
    if (false) {
      if (_activatedJobs.size() > 0
       || _terminatedJobs.size() > 0
       || _filteredDecisions.size() > 0) {
        closeTick();
      }
    }
    closeTick();

    _result = new Result(t, result);

    Data data = new Data(this);
    return data;
  }


  public static final class Data implements Tracker.Data {
    public final String trackerName = "History";
    public final Config config;
    public final List<Tick> ticks;
    public final Result result;

    private
    Data(History history)
    {
      config = history._config;
      ticks = history._ticks;
      result = history._result;
    }
  }


  public static final class Builder implements Tracker.Builder {
    private Boolean _coerceSameTickDecisions = false;

    public
    Builder ()
    {
    }

    @ConfigParam
    public Builder
    coerceSameTickDecisions (Boolean v)
    {
      _coerceSameTickDecisions = v;
      return this;
    }

    public History
    build ()
    {
      return new History(_coerceSameTickDecisions);
    }
  }
}

