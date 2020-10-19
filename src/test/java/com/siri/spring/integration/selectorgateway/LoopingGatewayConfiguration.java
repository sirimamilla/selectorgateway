package com.siri.spring.integration.selectorgateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;

@Configuration
public class LoopingGatewayConfiguration {

  @Bean
  public IntegrationFlow loopingGateway() {
    return flow ->
        flow.log()
            .gateway(
                "loopingInner.input",
                e ->
                    e.advice(new SelectingRequestHandlerAdvice("filter", true))
                        .advice(
                            new LoopingRequestHandlerAdvice(
                                m -> (3 > (int) m.getHeaders().get("v1")), "v1")))
            .log()
            .bridge();
  }

  @Bean
  public IntegrationFlow loopingInner() {
    return flow -> flow.transform(Message.class, this::upperCase).log().bridge();
  }

  public String upperCase(Message<String> message) {

    if(message.getPayload().equals("TEST1")){
      throw new RuntimeException("Intentional Error thrown to break loop");
    }

    return message.getPayload().toUpperCase();
  }
}
