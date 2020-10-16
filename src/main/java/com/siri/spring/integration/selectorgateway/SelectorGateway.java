package com.siri.spring.integration.selectorgateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.Message;

@Configuration
public class SelectorGateway {

    private static boolean fitlerCondition(Message m) {
        return "true".equals(m.getHeaders().get("filter"));
    }

    @Bean
    public IntegrationFlow selectingGateway(){

        return flow -> flow.log()
                .filter(Message.class, SelectorGateway::fitlerCondition, e->e.discardChannel("filterchannel"))
                .gateway("upperCase.input")
                .channel(c->c.direct("filterchannel"))
                .bridge();
    }

    @Bean
    public IntegrationFlow upperCase(){
        return flow->flow.<String, String>transform(m->m.toUpperCase())
                .log()
                .bridge();
    }

    @Bean
    public IntegrationFlow selectingGatewayInterceptor(){
        return  flow->flow.log()
                .gateway("upperCase.input", e->e.advice(new SelectingRequestHandlerAdvice("filter", true)))
                .log().bridge();
    }
}
