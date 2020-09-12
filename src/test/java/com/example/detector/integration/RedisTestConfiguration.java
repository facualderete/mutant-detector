package com.example.detector.integration;

import com.example.detector.configuration.AbstractRedisConfiguration;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class RedisTestConfiguration extends AbstractRedisConfiguration {

  @Bean
  @Primary
  public RedissonClient getRedissonClient() {
    return Redisson.create(getConfig());
  }

  @Override
  protected String getRedisUrl() {
    return String.format("redis://%s:%s",
        System.getProperty("REDIS_TEST_HOST"), System.getProperty("REDIS_TEST_PORT"));
  }
}
