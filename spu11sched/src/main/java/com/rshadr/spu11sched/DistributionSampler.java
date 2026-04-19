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
  private final int[] _delayTable;
  private final int _delayTableSize;
  private final int _freqScale;


  protected
  DistributionSampler (Distribution distribution,
                       int maxDelay,
                       int freqScale) {
    _distribution = distribution;
    _maxDelay = maxDelay;
    _freqScale = freqScale;
    _random = new Random();

    int delayTableSize = 0;
    int[] delayFreqsScaled = new int[maxDelay + 1];
    for (int i = 0; i <= maxDelay; ++i) {
      delayFreqsScaled[i] =
       (int)(_freqScale * distribution.restartProbability(i));
      delayTableSize += delayFreqsScaled[i];
    }

    _delayTableSize = delayTableSize;

    _delayTable = new int[_delayTableSize];
    for (int i = 0, j = 0; i <= _maxDelay; ++i) {
      for (int k = 0; k < delayFreqsScaled[i]; ++k, ++j) {
        _delayTable[j] = i;
      }
    }
  }


  protected int
  getNext ()
  {
    int pos = _random.nextInt(0, _delayTableSize);
    return _delayTable[pos];
  }

  /*
   * XXX: CDF 0 to 50 (=prob. x or lower)
   * Binary search with double
   */
}

