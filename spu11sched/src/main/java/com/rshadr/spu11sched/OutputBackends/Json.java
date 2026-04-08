/*
 * Copyright 2026 (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched.OutputBackends;
import com.rshadr.spu11sched.*;
import com.fasterxml.jackson.databind.ObjectMapper;


public final class Json implements OutputBackend {
  private ObjectMapper _objectMapper;

  private
  Json ()
  {
    _objectMapper = new ObjectMapper();
  }


  public void
  outputRecords (Object[] outputData)
  throws java.io.IOException
  {
    for (int i = 0; i < outputData.length; ++i) {
      _objectMapper.writeValue(System.out, outputData[i]);
    }
  }


  public static final class Builder implements OutputBackend.Builder {
    public
    Builder ()
    {
    }

    public Json
    build ()
    {
      return new Json();
    }
  }
}

