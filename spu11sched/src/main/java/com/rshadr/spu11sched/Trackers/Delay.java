/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for more details
 */
package com.rshadr.spu11sched;
import java.util.List;
import java.util.ArrayList;


/* XXX RENAME: AddedSporadicDelay */
public final class Delay extends Tracker {
  private final List<List<Integer>> _delaysPerTasks =
   new ArrayList<List<Integer>>(Configuration.MAX_TASKS);

  private final int[] _minRestartInstants = new int[Configuration.MAX_TASKS];

  private
  Delay ()
  {
    for (int i = 0; i < _minRestartInstants.length; ++i) {
      _minRestartInstants[i] = 0;
    }
  }


  public void
  onInitialize (Configuration config)
  {
    for (Task t : config.getTasks()) {
      int index = t.priority();
      _delaysPerTasks.add(index, new ArrayList<Integer>());
      _minRestartInstants[index] = t.initialStartTime();
    }

  }


  public void
  onJobActivated (int t, Job job)
  {
    int index = job.getPriority();
    int delay = job.getStartInstant() - _minRestartInstants[index];

    _delaysPerTasks.get(index).add(delay);
  }


  public void
  onJobTerminated (int t, Job job)
  {
    int index = job.getPriority() - 1;

    _minRestartInstants[index] = job.getStartInstant() + job.getTask().minIAT();
  }


  public Data
  onFinish (int t, Simulation.Result result)
  {
    return new Data(this);
  }


  public static final class Data implements Tracker.Data {
    public final String trackerName = "Delay";
    public final List<Entry> entries;

    public static final record Entry(int taskId, List<Integer> delays) {}

    private
    Data (Delay delay)
    {
      entries = new ArrayList<Entry>();

      for (int i = 0; i < delay._delaysPerTasks.size(); ++i) {
        List<Integer> delays = delay._delaysPerTasks.get(i);

        if (delays == null) {
          continue;
        }

        entries.add(new Entry(i, delays));
      }
    }
  }


  public static final class Builder implements Tracker.Builder {
    public
    Builder ()
    {
    }


    public Delay
    build ()
    {
      return new Delay();
    }
  }

}

