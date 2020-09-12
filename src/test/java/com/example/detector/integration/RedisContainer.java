package com.example.detector.integration;

import org.testcontainers.containers.GenericContainer;

public class RedisContainer extends GenericContainer<RedisContainer> {

  private static final String IMAGE_VERSION = "redis:3.2.1";
  private static RedisContainer container;

  public static RedisContainer getInstance() {
    if (container == null) {
      container = new RedisContainer();
    }
    return container;
  }

  public RedisContainer() {
    super(IMAGE_VERSION);
    withExposedPorts(6379);
  }

  @Override
  public void start() {
    super.start();
    System.setProperty("REDIS_TEST_HOST", container.getHost());
    System.setProperty("REDIS_TEST_PORT", container.getFirstMappedPort().toString());
  }
}
