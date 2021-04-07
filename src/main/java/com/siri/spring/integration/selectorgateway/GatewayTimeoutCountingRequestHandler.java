package com.siri.spring.integration.selectorgateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice;
import org.springframework.messaging.Message;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class GatewayTimeoutCountingRequestHandler extends AbstractRequestHandlerAdvice {

  AtomicInteger requestCounter = new AtomicInteger();
  AtomicInteger timeoutCounter = new AtomicInteger();
  String beanName;

  public GatewayTimeoutCountingRequestHandler(String beanName) {
    this.beanName = beanName;
  }

  @Override
  protected Object doInvoke(ExecutionCallback callback, Object target, Message<?> message) {

    requestCounter.incrementAndGet();
    Object o = callback.execute();

    if (Objects.isNull(o)) {
      timeoutCounter.incrementAndGet();
      log.error(
          "message=TimeoutOccuredInGateway bean={} requestCount={}, timeoutCount={}, timeoutPercent={}",
          beanName,
          requestCounter,
          timeoutCounter,
          (timeoutCounter.get() / requestCounter.get() * 100));
    }
    return o;
  }
}
