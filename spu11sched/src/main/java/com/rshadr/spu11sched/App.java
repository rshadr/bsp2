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
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.logging.Logger;
import java.util.logging.Level;


public class App
{
  private static final Logger LOGGER =
   Logger.getLogger(App.class.getName());

  static private InputStream
  makeInStreamForFile (String fileName)
  throws FileNotFoundException
  {
    if ("-".equals(fileName)) {
      return System.in;
    }

    FileInputStream stream = new FileInputStream(fileName);
    return stream;
  }


  static private Configuration.Builder
  parseConfiguration (InputStream stream)
  throws Exception
  {
    ConfigurationJsonParser cfgJsonParser = new ConfigurationJsonParser();

    Configuration.Builder configBuilder = cfgJsonParser.parseStream(stream);
    return configBuilder;
  }
  


  public static void
  main (String[] args)
  throws Exception
  {
    if (args.length != 1) {
      LOGGER.log(Level.SEVERE,
       "Bad argument count; must be exactly 1");
      System.exit(-1);
    }

/*
    Scanner scanner = App.makeScannerForFile(args[0]);
    Configuration.Builder configBuilder = App.parseConfig(scanner);
*/
    InputStream stream = App.makeInStreamForFile(args[0]);
    Configuration.Builder configBuilder = App.parseConfiguration(stream);

    Simulation sim = Simulation.withConfigAndSeed(configBuilder, 121);

    switch (sim.run()) {
      case SUCCESS: {
        break;
      }
      case DEADLINE_MISSED: {
        break;
      }
    }

    sim.output();
  }
}

