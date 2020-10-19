package com.siri.spring.integration.selectorgateway;

import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.integration.handler.advice.CacheRequestHandlerAdvice;
import org.springframework.messaging.Message;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class CacheRequestHandlerAdviceBuilder {

  private String name;
  private String ttl;
  private Function<Message<?>, ?> keyFunction;
    private String unless;

    public static CacheRequestHandlerAdviceBuilder builder() {
    return new CacheRequestHandlerAdviceBuilder();
  }

  public CacheRequestHandlerAdviceBuilder cacheName(String name) {
    this.name = name;
    return this;
  }
  public CacheRequestHandlerAdviceBuilder unless(String unless) {
    this.unless = unless;
    return this;
  }

  public CacheRequestHandlerAdviceBuilder ttl(long duration, TimeUnit unit) {
    this.ttl = duration + parseTimeUnit(unit);
    return this;
  }

  public CacheRequestHandlerAdviceBuilder keyFunction(Function<Message<?>, ?> keyFunction) {
    this.keyFunction = keyFunction;
    return this;
  }

  public CacheRequestHandlerAdvice build(){

      CaffeineCacheManager cacheManager = new CaffeineCacheManager();
      cacheManager.setCacheSpecification("expireAfterWrite=" + ttl);
      CacheableOperation.Builder builder = new CacheableOperation.Builder();
      builder.setCacheName(name);
      builder.setUnless(unless);
      CacheableOperation operation = new CacheableOperation(builder);
      CacheRequestHandlerAdvice cacheRequestHandlerAdvice = new CacheRequestHandlerAdvice();
      cacheRequestHandlerAdvice.setCacheManager(cacheManager);
      cacheRequestHandlerAdvice.setCacheOperations(operation);
      cacheRequestHandlerAdvice.setKeyFunction(keyFunction);
      return cacheRequestHandlerAdvice;

  }

  /** Returns a parsed {@link TimeUnit} value. */
  static String parseTimeUnit(TimeUnit unit) {

    switch (unit.name()) {
      case "DAYS":
        return "d";
      case "HOURS":
        return "h";
      case "MINUTES":
        return "m";
      case "SECONDS":
        return "s";
      default:
        throw new IllegalArgumentException(String.format("Unit %s Not supported", unit.name()));
    }
  }
}
