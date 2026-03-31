/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;
import com.rshadr.spu11sched.Distributions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Hello world!
 */
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


  private static Configuration
  parseConfig (Scanner scanner)
  throws Exception
  {
    Configuration cfg = new Configuration();

    do {
      String firstLine = scanner.nextLine();
      String[] parts = firstLine.split(" ");
      assert parts.length == 4
       : "First line must contain exactly four tokens";


      int maxDuration = Integer.parseInt(parts[0]);
      assert maxDuration > 0 : "Max duration must be positive";

      int numProcessors = Integer.parseInt(parts[1]);
      assert numProcessors > 0 : "Number of processors must be positive";


      Distribution distribution = null;
      if ("geometric".equals(parts[2])) {
        double p = Double.parseDouble(parts[3]);
        distribution = new Geometric(p);
      } else if ("poisson".equals(parts[2])) {
        int lambda = Integer.parseInt(parts[3]);
        distribution = new Poisson(lambda);
      } else {
        throw new IllegalArgumentException("Invalid distribution name");
      }

      cfg.setMaxDuration(maxDuration);
      cfg.setNumProcessors(numProcessors);

    } while (false);

    while (scanner.hasNext()) {
      String[] parts = scanner.nextLine().split(" ");

      int a = Integer.parseInt(parts[0]);
      assert a >= 0
       : "Initial offset must be positive";

      int c = Integer.parseInt(parts[1]);
      assert c > 0
       : "WCET must be positive";

      int p = Integer.parseInt(parts[2]);
      assert p >= 0
       : "Task priority must be positive";

      int d = Integer.parseInt(parts[3]);
      assert d > 0 && d >= c
       : "Relative deadline cannot be lower than WCET";

      int t = Integer.parseInt(parts[4]);
      assert t > 0 && t >= d
       : "Minimum inter-arrival time must be at least deadline";

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
    Configuration config = App.parseConfig(scanner);

    Simulation sim = new Simulation(config);
    sim.run();
    sim.output();
  }
}

