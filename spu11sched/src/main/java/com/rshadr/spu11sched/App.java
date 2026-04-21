/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

import com.rshadr.spu11sched.Schedulers.*;
import com.rshadr.spu11sched.Distributions.*;
import com.rshadr.spu11sched.Trackers.*;
import com.rshadr.spu11sched.OutputBackends.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class App
{
  private static Scanner
  makeScannerForFile (String fileName)
  throws FileNotFoundException
  {
    if (fileName.equals("-"))
      return new Scanner(System.in);

    File file = new File(fileName);

    return new Scanner(file);
  }


  private static Configuration.Builder
  parseConfig (Scanner scanner)
  throws Exception
  {
    Configuration.Builder cfg = new Configuration.Builder();

    do {
      String firstLine = scanner.nextLine();
      String[] parts = firstLine.split(" ");
      if (parts.length != 4) {
        throw new IllegalArgumentException("First line must contain 4 tokens");
      }

      int maxDuration = Integer.parseInt(parts[0]);
      cfg.maxDuration(maxDuration);

      /* XXX */
      cfg.maxSporadicDelay(0);

      int numProcessors = Integer.parseInt(parts[1]);
      cfg.numProcessors(numProcessors);

      /*
       * XXX: Read scheduler off config
       */
      cfg.schedulerBuilder(new FixedPriority.Builder());

      Distribution distribution = null;
      if ("null".equals(parts[2])) {
        /* ignore rest */
        distribution = new Null();
      } else if ("geometric".equals(parts[2])) {
        double p = Double.parseDouble(parts[3]);
        distribution = new Geometric.Builder().p(p).build();
      } else if ("poisson".equals(parts[2])) {
        int lambda = Integer.parseInt(parts[3]);
        distribution = new Poisson.Builder().lambda(lambda).build();
      } else {
        throw new IllegalArgumentException("Invalid distribution name");
      }
      cfg.distribution(distribution);

      /*
       * XXX: Read trackers off config
       */
      cfg.addTrackerBuilder(new History.Builder());

      /*
       * XXX: Read output backend off config
       */
      Json.Builder jsonBuilder = new Json.Builder();
      cfg.outputBackendBuilder(jsonBuilder);


    } while (false);

    while (scanner.hasNext()) {
      String[] parts = scanner.nextLine().split(" ");

      int a = Integer.parseInt(parts[0]);
      int c = Integer.parseInt(parts[1]);
      int p = Integer.parseInt(parts[2]);
      int d = Integer.parseInt(parts[3]);
      int t = Integer.parseInt(parts[4]);

      Task task = new Task(p, c, a, d, t);
      cfg.addTask(task);
    }

    return cfg;
  }
  


  public static void
  main (String[] args)
  throws Exception
  {
    if (args.length != 1) {
      System.err.println("Bad argument count; must be exactly 1");
      System.exit(-1);
    }

    Scanner scanner = App.makeScannerForFile(args[0]);
    Configuration.Builder configBuilder = App.parseConfig(scanner);

    Simulation sim = Simulation.withConfigAndSeed(configBuilder, 121);
    try {
      sim.run();
    } catch (DeadlineMissedException e) {}
    sim.output();
  }
}

