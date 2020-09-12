package com.example.detector.configuration;

import org.redisson.config.Config;

public abstract class AbstractRedisConfiguration {

  private static final int REDIS_REQUEST_TIMEOUT = 10000;
  private static final int REDIS_CONNECTION_TIMEOUT = 10000;
  private static final int REDIS_RETRY_ATTEMPTS = 10;
  private static final String REDIS_CLIENT_NAME = "mutant-redis-client";

  protected Config getConfig() {
    Config config = new Config();
    // TODO: log url!
    config.useSingleServer()
        .setClientName(REDIS_CLIENT_NAME)
        .setAddress(getRedisUrl())
        .setPassword(getRedisPassword())
        .setTimeout(REDIS_REQUEST_TIMEOUT)
        .setConnectTimeout(REDIS_CONNECTION_TIMEOUT)
        .setRetryAttempts(REDIS_RETRY_ATTEMPTS);
    return config;
  }

  protected abstract String getRedisUrl();

  protected abstract String getRedisPassword();
}
