package com.example.detector.redis;

import lombok.AllArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class RedisCache {

  private static final String MUTANT_COUNT_KEY = "mutant-count";
  private static final String HUMAN_COUNT_KEY = "human-count";
  private static final String REDIS_MAP = "evaluation-result";

  private final RedissonClient redissonClient;

  public long getCountMutantCount() {
    return redissonClient.getAtomicLong(MUTANT_COUNT_KEY).get();
  }

  public long getCountHumanCount() {
    return redissonClient.getAtomicLong(HUMAN_COUNT_KEY).get();
  }

  public long incrementCount(boolean isMutant) {
    String countKey = isMutant ? MUTANT_COUNT_KEY : HUMAN_COUNT_KEY;
    // TODO: log which key is being incremented
    RAtomicLong count = redissonClient.getAtomicLong(countKey);
    return count.incrementAndGet();
  }

  public void writeData(String hash, boolean isMutant) {
    getMap().put(hash, isMutant);
  }

  public boolean readData(String hash) {
    return getMap().get(hash);
  }

  public boolean existsData(String hash) {
    return getMap().containsKey(hash);
  }

  private RMap<String, Boolean> getMap() {
    return redissonClient.getMap(REDIS_MAP);
  }

}
