package com.example.detector.configuration;

import java.util.Optional;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration extends AbstractRedisConfiguration {

  private static final String REDIS_ENV_URL = "REDISCLOUD_URL";
  private static final String REDIS_ENV_PASSWORD = "REDISCLOUD_URL";
  private static final String REDIS_LOCAL_URL = "redis://127.0.0.1:6379";

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
