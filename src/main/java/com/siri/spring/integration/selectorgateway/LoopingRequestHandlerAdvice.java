package com.siri.spring.integration.selectorgateway;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.core.GenericSelector;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

public class LoopingRequestHandlerAdvice extends IntegrationObjectSupport
        implements MethodInterceptor {

    GenericSelector<Message> condition=m->false;
    String loopVariable="loopVariable";

    public LoopingRequestHandlerAdvice(GenericSelector<Message> condition, String loopVariable) {
        this.condition = condition;
        this.loopVariable = loopVariable;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        try{
        List list=new ArrayList<>();
        Message<?> message;
        if (1==invocation.getArguments().length && invocation.getArguments()[0] instanceof Message){
             message = (Message<?>) invocation.getArguments()[0];

            do{
                message= incrementCounter(message);
                ((ProxyMethodInvocation)invocation).setArguments(message);
                Object proceed = invocation.proceed();

                if (proceed instanceof Message) {
                    message=(Message) proceed;
                    list.add(message.getPayload());
                }
            }while(condition.accept(message));

            return list;
        }

        else {
            return invocation.proceed();
        }
        }catch (Throwable e){
            e.printStackTrace();
            throw e;
        }

    }

    private Message<?> incrementCounter(Message<?> message) {
        Integer count = message.getHeaders().containsKey(loopVariable)? (Integer) message.getHeaders().get(loopVariable) + 1: 0;
        return MessageBuilder.fromMessage(message).setHeader(loopVariable, count).build();
    }
}
