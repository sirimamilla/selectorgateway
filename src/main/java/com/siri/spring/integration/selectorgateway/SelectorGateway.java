package com.siri.spring.integration.selectorgateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;

// @EnableCaching
@Configuration
public class SelectorGateway {

  @Autowired GenericApplicationContext context;



  private static boolean fitlerCondition(Message<?> m) {
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

}
