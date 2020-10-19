package com.siri.spring.integration.selectorgateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.advice.CacheRequestHandlerAdvice;
import org.springframework.messaging.Message;

import java.util.concurrent.atomic.AtomicInteger;

// @EnableCaching
@Configuration
public class SelectorGateway {

  @Autowired GenericApplicationContext context;

  AtomicInteger integer = new AtomicInteger();

  private static boolean fitlerCondition(Message m) {
    return "true".equals(m.getHeaders().get("filter"));
  }

  @Bean
  public IntegrationFlow selectingGateway() {

    return flow ->
        flow.log()
            .filter(
                Message.class,
                SelectorGateway::fitlerCondition,
                e -> e.discardChannel("filterchannel"))
            .gateway("upperCase.input")
            .channel(c -> c.direct("filterchannel"))
            .bridge();
  }

  @Bean
  public IntegrationFlow upperCase() {
    return flow -> flow.transform(Message.class, this::upperCase).log().bridge();
  }

  public String upperCase(Message<String> message) {
    //        if(2==(int)message.getHeaders().get("v1")){
    //            throw new RuntimeException();
    //        }
    return message.getPayload().toUpperCase();
  }

  @Bean
  public IntegrationFlow selectingGatewayInterceptor() {
    return flow ->
        flow.log()
            .gateway(
                "upperCase.input", e -> e.advice(new SelectingRequestHandlerAdvice("filter", true)))
            .log()
            .bridge();
  }


  @Bean
  CacheRequestHandlerAdvice cacheRequestHandlerAdvice() {
    String name="test";
    CaffeineCacheManager cacheManager = new CaffeineCacheManager(name);
    cacheManager.setCacheSpecification("expireAfterWrite=1s");
    CacheableOperation.Builder builder = new CacheableOperation.Builder();
    builder.setCacheName(name);
    CacheableOperation operation = new CacheableOperation(builder);
    CacheRequestHandlerAdvice cacheRequestHandlerAdvice = new CacheRequestHandlerAdvice();
    cacheRequestHandlerAdvice.setCacheManager(cacheManager);
    cacheRequestHandlerAdvice.setCacheOperations(operation);
    cacheRequestHandlerAdvice.setKeyFunction(m -> m.getPayload());

    BeanDefinition beanDefinition= new RootBeanDefinition(CacheRequestHandlerAdvice.class, ()->cacheRequestHandlerAdvice);
    beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
//    beanDefinition.setPrimary(tr);
    context.registerBeanDefinition(name, beanDefinition);

    return cacheRequestHandlerAdvice;
  }

  @Bean
  CacheRequestHandlerAdvice cacheRequestHandlerAdviceTest1() {

    CaffeineCacheManager cacheManager = new CaffeineCacheManager("test1");
    cacheManager.setCacheSpecification("expireAfterWrite=30m");
    CacheableOperation.Builder builder = new CacheableOperation.Builder();
    builder.setCacheName("test1");
    CacheableOperation operation = new CacheableOperation(builder);
    CacheRequestHandlerAdvice cacheRequestHandlerAdvice = new CacheRequestHandlerAdvice();
    cacheRequestHandlerAdvice.setCacheManager(cacheManager);
    cacheRequestHandlerAdvice.setCacheOperations(operation);
    cacheRequestHandlerAdvice.setKeyFunction(m -> m.getPayload());
    return cacheRequestHandlerAdvice;
  }

  @Bean
  public IntegrationFlow cachingGateway() {

    //        CacheRequestHandlerAdvice cacheRequestHandlerAdvice = new
    // CacheRequestHandlerAdvice("otherCache");
    //        cacheRequestHandlerAdvice.setKeyFunction(m->m.getPayload());
    //

    return flow ->
        flow.log()
            .gateway("countingAdvice.input", e -> e.advice(cacheRequestHandlerAdvice()))
            .log()
            .bridge();
  }

  @Bean
  public IntegrationFlow cachingGatewayTest1() {

    //        CacheRequestHandlerAdvice cacheRequestHandlerAdvice = new
    // CacheRequestHandlerAdvice("otherCache");
    //        cacheRequestHandlerAdvice.setKeyFunction(m->m.getPayload());
    //

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

  @Bean
  public IntegrationFlow loopingGateway() {
    return flow ->
        flow.log()
            .gateway(
                "upperCase.input",
                e ->
                    e.advice(new SelectingRequestHandlerAdvice("filter", true))
                        .advice(
                            new LoopingRequestHandlerAdvice(
                                m -> (3 > (int) m.getHeaders().get("v1")), "v1")))
            .log()
            .bridge();
  }
}
