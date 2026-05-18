/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for more details
 */
package com.rshadr.spu11sched;
import java.util.List;


public final class SlackTime extends Tracker {
  static private final record Entry(
    Task task,
    int[] table) {}

  private final Entry[] _entries;
  private int _numTasks;

  private
  SlackTime ()
  {
    _entries = new Entry[Configuration.MAX_TASKS];
  }


  public void
  onInitialize (Configuration config)
  {
    List<Task> tasks = config.getTasks();

    for (Task task : tasks) {
      int worstSlackTime = task.relativeDeadline() - task.wcet() + 1;

      _entries[task.priority()] =
       new Entry(task, new int[worstSlackTime + 1]);
    }

    _numTasks = tasks.size();
  }


  private int
  getSlack (int t, Job job)
  {
    return (job.getAbsoluteDeadline() - t - 1);
  }


  public void
  onJobTerminated (int t, Job job)
  {
    int slack = getSlack(t, job);
    Entry e = _entries[job.getPriority()];
    e.table()[slack] += 1;
  }


  public Data
  onFinish (int t, Simulation.Result result)
  {
    return new Data(this);
  }


  public static final class Data implements Tracker.Data {
    static public final record TaskStats(
      int jobId,
      int[] frequencies
    ){}

    public final String trackerName;
    public final TaskStats[] stats;

    private
    Data(SlackTime slackTime)
    {
      trackerName = "SlackTime";
      stats = new TaskStats[slackTime._numTasks];

      int j = 0;
      for (int i = 0; i < slackTime._entries.length; ++i) {
        Entry e = slackTime._entries[i];
        if (e == null) {
          continue;
        }

        stats[j] = new TaskStats(e.task().priority(), e.table());
        ++j;
      }
    }
  }


  public static final class Builder implements Tracker.Builder {
    public
    Builder()
    {
    }


    public SlackTime
    build ()
    {
      return new SlackTime();
    }
  }
}

