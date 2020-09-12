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

    double ratio = 100.0;
    if (humanCount != 0L) {
      ratio = (mutantCount * 100.0) / humanCount;
    }

    return Stats.builder()
        .countMutantDna(mutantCount)
        .countHumanDna(humanCount)
        .ratio(ratio)
        .build();
  }
}
