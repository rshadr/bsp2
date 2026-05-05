/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

class ConfigurationJsonParser
{
  static private final class CfgProxy {
    public int maxDuration;
    public int numProcessors;
    public int maxSporadicDelay;
    public String scheduler;
    public String distribution;

    public List<Task> tasks;

    public List<String> trackers;
  }

  private CfgProxy _cfgProxy;
  private ObjectMapper _mapper = new ObjectMapper();


  protected
  ConfigurationJsonParser ()
  {
  }


  protected void
  parseStream (java.io.InputStream inputStream)
  throws java.io.IOException
  {
    _cfgProxy = _mapper.readValue(inputStream, CfgProxy.class);
  }


  protected Configuration.Builder
  toConfigurationBuilder ()
  throws IllegalStateException, IllegalArgumentException
  {
    if (_cfgProxy == null) {
      throw new IllegalStateException("missing cfg proxy");
    }

    Configuration.Builder cfgBuilder = new Configuration.Builder();

    cfgBuilder.maxDuration(_cfgProxy.maxDuration);
    cfgBuilder.numProcessors(_cfgProxy.numProcessors);
    cfgBuilder.maxSporadicDelay(_cfgProxy.maxSporadicDelay);
    // XXX

    for (Task t : _cfgProxy.tasks) {
      cfgBuilder.addTask(t);
    }

    for (String tr : _cfgProxy.trackers) {
      /* XXX */
    }

    return cfgBuilder;
  }
}

