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
  private final double _fixedMean;


  protected
  DistributionSampler (Distribution distribution,
                       int maxDelay,
                       long seed) {
    _distribution = distribution;
    _maxDelay = maxDelay;
    _seed = seed;
    _random = new Random(_seed);
    _cdfArray = new double[_maxDelay + 1];

    final double maxDelayInv = 1.0 / _maxDelay;
    double cdfY = 0.0;
    double fixedMean = 0.0;
    for (int i = 0; i <= _maxDelay; ++i) {
      double pi = _distribution.restartProbability(i);
      cdfY += pi;
      _cdfArray[i] = cdfY;
      //System.out.println("[CDF] "+i+": "+cdfY);

      /* XXX: mean formula??? */
      fixedMean += pi;
    }
    fixedMean /= _cdfArray[_maxDelay];

    _fixedMean = fixedMean;
  }


  protected int
  getNext ()
  {
    double v = _random.nextDouble() * _cdfArray[_maxDelay];

    int i = 0;
    for (; i <= _maxDelay; ++i) {
      if (_cdfArray[i] > v) {
        return i;
      }
    }

    /* unreachable? */
    return _maxDelay;
  }


  protected double
  getFixedMean ()
  {
    return _fixedMean;
  }
}

