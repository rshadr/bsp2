/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;
import java.util.Random;


final class DistributionSampler {
  private final Distribution _distribution;
  private final Random _random;
  private final int _maxDelay;
  private final double[] _cdfArray;
  private final long _seed;


  protected
  DistributionSampler (Distribution distribution,
                       int maxDelay,
                       long seed) {
    _distribution = distribution;
    _maxDelay = maxDelay;
    _seed = seed;
    _random = new Random(_seed);
    _cdfArray = new double[_maxDelay + 1];

    double cdfY = 0.0;
    for (int i = 0; i <= _maxDelay; ++i) {
      cdfY += _distribution.restartProbability(i);
      _cdfArray[i] = cdfY;
    }
  }


  protected int
  getNext ()
  {
    double v = _random.nextDouble();

    int i = 0;
    for (; i <= _maxDelay; ++i) {
      if (_cdfArray[i] > v) {
        return i;
      }
    }

    /* unreachable? */
    return _maxDelay;
  }
}

