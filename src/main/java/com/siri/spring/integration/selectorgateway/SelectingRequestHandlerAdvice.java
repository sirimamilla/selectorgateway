package com.siri.spring.integration.selectorgateway;

import lombok.Builder;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.handler.advice.AbstractRequestHandlerAdvice;
import org.springframework.messaging.Message;

@Builder
public class SelectingRequestHandlerAdvice extends AbstractRequestHandlerAdvice {

    GenericSelector<Message> selector;




    public SelectingRequestHandlerAdvice(GenericSelector<Message> selector) {
        this.selector = selector;
    }
    public SelectingRequestHandlerAdvice(String headername, Object value) {
        selector= m->value.equals(m.getHeaders().get(headername));
    }

    @Override
    protected Object doInvoke(ExecutionCallback callback, Object target, Message<?> message) {
        Boolean execute=true;
        if(selector!=null){
            execute = selector.accept(message);
        }


        if(execute){
            Object response = callback.execute();
            return response;
        }


        return message;
    }


}
