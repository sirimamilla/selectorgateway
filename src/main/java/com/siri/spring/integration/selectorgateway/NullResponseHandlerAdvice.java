package com.siri.spring.integration.selectorgateway;

import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice;
import org.springframework.messaging.Message;

import java.util.Objects;

public class NullResponseHandlerAdvice extends AbstractRequestHandlerAdvice {





    @Override
    protected Object doInvoke(ExecutionCallback callback, Object target, Message<?> message) {

        Object o= callback.execute();

    if (Objects.isNull(o)) {
        throw new RuntimeException();
    }

        return o;

    }


}
