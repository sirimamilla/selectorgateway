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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// @EnableCaching
@Configuration
public class SelectorGateway {

  @Autowired GenericApplicationContext context;



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




//  @Bean
//  public IntegrationFlow loopingGateway() {
//    return flow ->
//        flow.log()
//            .gateway(
//                "upperCase.input",
//                e ->
//                    e.advice(new SelectingRequestHandlerAdvice("filter", true))
//                        .advice(
//                            new LoopingRequestHandlerAdvice(
//                                m -> (3 > (int) m.getHeaders().get("v1")), "v1")))
//            .log()
//            .bridge();
//  }
}
