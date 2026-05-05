/*
 * Copyright 2026 (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched.OutputBackends;
import java.util.List;
import com.rshadr.spu11sched.*;
import com.fasterxml.jackson.databind.ObjectMapper;


public final class Json implements OutputBackend {
  private ObjectMapper _objectMapper;
  private java.io.OutputStream _outputStream;

  private
  Json (java.io.OutputStream outputStream)
  {
    _objectMapper = new ObjectMapper();
    _outputStream = outputStream;
  }


  public void
  outputTrackerDatas (List<Tracker.Data> trackerDatas)
  throws java.io.IOException
  {
    _objectMapper
     .writerWithDefaultPrettyPrinter()
     .writeValue(_outputStream, trackerDatas);
  }


  public static final class Builder implements OutputBackend.Builder {
    private java.io.OutputStream _outputStream;

    public
    Builder ()
    {
      _outputStream = System.out;
    }


    public Builder
    withOutputStream (java.io.OutputStream outputStream)
    {
      _outputStream = outputStream;
      return this;
    }


    public Json
    build ()
    {
      return new Json(_outputStream);
    }
  }
}

