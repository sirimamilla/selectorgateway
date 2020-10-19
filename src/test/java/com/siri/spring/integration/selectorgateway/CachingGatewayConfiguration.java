package com.siri.spring.integration.selectorgateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.advice.CacheRequestHandlerAdvice;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
public class CachingGatewayConfiguration {

    AtomicInteger integer = new AtomicInteger();

    @Bean
    CacheRequestHandlerAdvice cacheRequestHandlerAdvice() {
        return CacheRequestHandlerAdviceBuilder.builder()
                .ttl(1, TimeUnit.SECONDS)
                .keyFunction(m->true)
                .cacheName("test1")
                .build();
    }

    @Bean
    CacheRequestHandlerAdvice cacheRequestHandlerAdviceTest1() {

        return CacheRequestHandlerAdviceBuilder.builder()
                .ttl(30, TimeUnit.MINUTES)
                .keyFunction(m->true)
                .unless(null)
                .cacheName("test1")
                .build();
    }

    @Bean
    public IntegrationFlow cachingGateway() {

        return flow ->
                flow.log()
                        .gateway("countingAdvice.input", e -> e.advice(cacheRequestHandlerAdvice()))
                        .log()
                        .bridge();
    }

    @Bean
    public IntegrationFlow cachingGatewayTest1() {

        return flow ->
                flow.log()
                        .gateway("countingAdvice.input", e -> e.advice(cacheRequestHandlerAdviceTest1()))
                        .log()
                        .bridge();
    }

    @Bean
    public IntegrationFlow countingAdvice() {
        return f -> f.transform(m -> integer.incrementAndGet());
    }
}
