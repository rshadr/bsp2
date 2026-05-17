/*
 * Copyright 2026 rshadr (rshadr@assembly-cave.tw)
 * See LICENSE for details
 */
package com.rshadr.spu11sched;
import com.rshadr.spu11sched.Distributions.*;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.lang.Math;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class DistributionSamplerTest {
  private static final int MAX_DELAY_ = 100;
  private static final int NUM_SAMPLES_ = 10000;
  private static final double INV_NUM_SAMPLES_ = 1.0 / NUM_SAMPLES_;

  /* NOTE: it's pointless to use specific seeds */
  //private static final long SEED_ = 1337;

  private static final double EPSILON_ = 0.0001;
  private final ThreadLocalRandom _random = ThreadLocalRandom.current();


  private double
  getSampledMean_ (DistributionSampler sampler, int[] sample_counts)
  {
    // assert sample_counts.length == MAX_DELAY_ + 1;

    Arrays.fill(sample_counts, 0);

    for (int i = 0; i < NUM_SAMPLES_; ++i) {
      int x = sampler.getNext();
      ++sample_counts[x];
    }

    double sampled_mean = 0.0;

    for (int i = 0; i < sample_counts.length; ++i) {
      int xi = sample_counts[i];
      sampled_mean += xi * INV_NUM_SAMPLES_;
    }

    return sampled_mean;
  }


  @Test
  public void
  testGeometric ()
  {
    Geometric.Builder geometricBuilder = new Geometric.Builder();
    int[] sample_counts = new int[MAX_DELAY_ + 1];

    for (double p_ = 0.01; p_ <= 1.00; p_ += 0.01) {
      final double p = p_;
      geometricBuilder.p(p);

      Distribution dist = geometricBuilder.build();
      DistributionSampler sampler =
       new DistributionSampler(dist, MAX_DELAY_, _random.nextLong());

      // final double mean = (1.0 - p) / p;
      final double mean = sampler.getFixedMean();
      final double sampled_mean = getSampledMean_(sampler, sample_counts);

      assertTrue( Math.abs(mean - sampled_mean) < EPSILON_,
       () -> "[p="+p+"] mean="+mean+"; sampled_mean="+sampled_mean);
    }
  }


  @Test
  public void
  testPoisson ()
  {
    Poisson.Builder poissonBuilder = new Poisson.Builder();
    int[] sample_counts = new int[MAX_DELAY_ + 1];

    for (int lambda_ = 1; lambda_ <= MAX_DELAY_; ++lambda_) {
      final int lambda = lambda_;
      poissonBuilder.lambda(lambda);

      Distribution dist = poissonBuilder.build();
      DistributionSampler sampler =
       new DistributionSampler(dist, MAX_DELAY_, _random.nextLong());

      final double mean = sampler.getFixedMean();
      final double sampled_mean = getSampledMean_(sampler, sample_counts);

      assertTrue( Math.abs(mean - sampled_mean) < EPSILON_,
       () -> "[lambda="+lambda+"] mean="+mean+"; sampled_mean="+sampled_mean+"");
    }
  }


  /*
   * NOTE: No test for Null since there is no sporadicity
   */
}

