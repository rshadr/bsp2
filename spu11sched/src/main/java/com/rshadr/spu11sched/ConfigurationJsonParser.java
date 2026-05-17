/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;

import java.lang.Class;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rshadr.spu11sched.Schedulers.*;
import com.rshadr.spu11sched.Distributions.*;
import com.rshadr.spu11sched.OutputBackends.*;
import com.rshadr.spu11sched.Trackers.*;

class ConfigurationJsonParser
{
  static private final record CompositeProxy(
    String name,
    ObjectNode options
  ) {}

  static private final class CfgProxy {
    public int maxDuration;
    public int numProcessors;
    public int maxSporadicDelay;
    public CompositeProxy scheduler;
    public CompositeProxy distribution;

    public List<Task> tasks;

    public CompositeProxy outputBackend;
    public List<CompositeProxy> trackers;
  }

  static private final Class[] _schedulerClasses = {
    FixedPriority.class
  };

  static private final Class[] _distributionClasses = {
    Geometric.class,
    Poisson.class,
    Null.class
  };

  static private final Class[] _outputBackendClasses = {
    Json.class
  };

  static private final Class[] _trackerClasses = {
    History.class,
    SlackTime.class,
    Delay.class
  };

  private ObjectMapper _mapper = new ObjectMapper();


  protected
  ConfigurationJsonParser ()
  {
  }


  private Object
  compositeFromProxy (Class compositeClass, CompositeProxy proxy)
  throws
   IllegalAccessException,
   IllegalArgumentException,
   NoSuchMethodException,
   InstantiationException,
   InvocationTargetException
  {
    /*
     * Get builder
     */
    Class builderClass = null;
    for (Class k : compositeClass.getClasses()) {
      if ("Builder".equals(k.getSimpleName())) {
        builderClass = k;
        break;
      }
    }

    if (builderClass == null) {
      throw new IllegalArgumentException("Builder class cannot be null");
    }

    Object builder = builderClass.getConstructor().newInstance();

    /*
     * Get builder functions
     */
    Method[] builderModifiers = builderClass.getMethods();
    for (Map.Entry<String,JsonNode> prop : proxy.options().properties()) {
      if ("build".equals(prop.getKey())) {
        throw new IllegalArgumentException(
         "Cannot have a property with name build");
      }

      Boolean modFound = false;
      for (Method mod : builderModifiers) {
        if (mod.getAnnotation(ConfigParam.class) != null
         && mod.getName().equals(prop.getKey())) {

          Class[] paramTypes =
           mod.getParameterTypes();
          if (paramTypes.length != 1) {
            throw new IllegalStateException(
              "builder modifier should have exactly one argument"
            + "(found "+paramTypes.length+")"
            );
          }
          Class modType = paramTypes[0];

          Object v = null;

          switch (prop.getValue().getNodeType()) {
            case BOOLEAN: {
              v = prop.getValue().asBoolean();
              break;
            }

            case STRING: {
              v = prop.getValue().asText();
              break;
            }

            case NUMBER: {
              if (prop.getValue().isDouble()) {
                v = prop.getValue().doubleValue();
                break;
              }

              if (prop.getValue().isInt()) {
                v = prop.getValue().intValue();
                break;
              }

              throw new IllegalArgumentException(
               "bad number type"
              );
            }

            default: {
              throw new IllegalArgumentException(
                "invalid builder modifier value type: "
               +prop.getValue().getNodeType()
              );
            }
          }

          if (! modType.equals(v.getClass()) ) {
            throw new IllegalArgumentException(
             "type mismatch for builder modifier"
            );
          }

          mod.invoke(builder, v);

          modFound = true;
          break;
        }
      }

      if (!modFound) {
        throw new IllegalArgumentException("No matching modifier found");
      }
    }

    return builder;
  }


  protected Configuration.Builder
  parseStream (java.io.InputStream inputStream)
  throws
   java.io.IOException,
   IllegalArgumentException,
   IllegalAccessException,
   NoSuchMethodException,
   InstantiationException,
   InvocationTargetException
  {
    CfgProxy _cfgProxy = _mapper.readValue(inputStream, CfgProxy.class);

    Configuration.Builder cfgBuilder = new Configuration.Builder();

    cfgBuilder.maxDuration(_cfgProxy.maxDuration);
    cfgBuilder.numProcessors(_cfgProxy.numProcessors);
    cfgBuilder.maxSporadicDelay(_cfgProxy.maxSporadicDelay);

    for (Class klass : _schedulerClasses) {
      if (! klass.getSimpleName().equals(_cfgProxy.scheduler.name()) ) {
        continue;
      }

      Scheduler.Builder schedulerBuilder = (Scheduler.Builder)
       compositeFromProxy(klass, _cfgProxy.scheduler);
      cfgBuilder.schedulerBuilder(schedulerBuilder);
      break;
    }

 
    for (Class klass : _distributionClasses) {
      if (! klass.getSimpleName().equals(
          _cfgProxy.distribution.name()) ) {
        continue;
      }

      Distribution.Builder distributionBuilder = (Distribution.Builder)
       compositeFromProxy(klass, _cfgProxy.distribution);
      cfgBuilder.distribution(distributionBuilder.build());
      break;
    }


    for (Class klass : _outputBackendClasses) {
      if (! klass.getSimpleName().equals(
          _cfgProxy.outputBackend.name()) ) {
        continue;
      }

      OutputBackend.Builder outputBackendBuilder =
       (OutputBackend.Builder)compositeFromProxy(
        klass, _cfgProxy.outputBackend);
      cfgBuilder.outputBackendBuilder(outputBackendBuilder);
      break;
    }


    for (CompositeProxy tr : _cfgProxy.trackers) {
      for (Class klass : _trackerClasses) {
        if (! klass.getSimpleName().equals(tr.name()) ) {
          continue;
        }

        Tracker.Builder trackerBuilder = (Tracker.Builder)compositeFromProxy(klass, tr);
        cfgBuilder.addTrackerBuilder(trackerBuilder);
        break;
      }
    }


    for (Task t : _cfgProxy.tasks) {
      cfgBuilder.addTask(t);
    }


    return cfgBuilder;
  }

}

