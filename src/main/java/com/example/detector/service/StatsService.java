package com.example.detector.service;

import com.example.detector.model.Stats;
import com.example.detector.redis.RedisCache;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class StatsService {

  private final RedisCache redisCache;

  public Stats getStats() {
    long mutantCount = redisCache.getCountMutantCount();
    long humanCount = redisCache.getCountHumanCount();

    double ratio = 100.0; // Avoid dividing by zero: if no humans, mutants ratio is 100%
    if (humanCount != 0L) {
      ratio = (mutantCount * 100.0) / humanCount; // get mutants/humans ratio
      ratio = Math.round(ratio * 100.0) / 100.0; // round to 2 decimals
    }

    return Stats.builder()
        .countMutantDna(mutantCount)
        .countHumanDna(humanCount)
        .ratio(ratio)
        .build();
  }
}
