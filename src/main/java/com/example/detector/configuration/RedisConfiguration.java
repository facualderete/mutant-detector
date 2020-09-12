package com.example.detector.configuration;

import java.util.Optional;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration extends AbstractRedisConfiguration {

  private static final String REDIS_ENV_URL = "REDISCLOUD_URL";
  private static final String REDIS_ENV_PASSWORD = "REDISCLOUD_PASSWORD";
  private static final String REDIS_LOCAL_URL = "redis://redis:6379"; // "redis" is the name in docker-compose

  @Bean
  public RedissonClient getRedissonClient() {
    return Redisson.create(getConfig());
  }

  @Override
  protected String getRedisUrl() {
    return Optional.ofNullable(System.getenv(REDIS_ENV_URL)).orElse(REDIS_LOCAL_URL);
  }

  @Override
  protected String getRedisPassword() {
    return System.getenv(REDIS_ENV_PASSWORD);
  }

}
