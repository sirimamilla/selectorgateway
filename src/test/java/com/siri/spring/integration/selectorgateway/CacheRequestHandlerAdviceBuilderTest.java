package com.siri.spring.integration.selectorgateway;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class CacheRequestHandlerAdviceBuilderTest {

    @Qualifier("cachingGateway.input")
    @Autowired
    MessageChannel cachingChannel;

    @Test
    void cahcingAdviceTest() throws InterruptedException {

        QueueChannel replychannel = new QueueChannel();
        Message message = MessageBuilder.withPayload("test").setReplyChannel(replychannel).build();

        cachingChannel.send(message);
        Message<?> respMessage = replychannel.receive(10_000L);

        assertNotNull(respMessage);
        assertEquals(1, respMessage.getPayload());
        cachingChannel.send(message);
        respMessage = replychannel.receive(10_000L);

        assertNotNull(respMessage);
        assertEquals(1, respMessage.getPayload());

        cachingChannel.send(message);
        respMessage = replychannel.receive(10_000L);

        assertNotNull(respMessage);
        assertEquals(1, respMessage.getPayload());
        cachingChannel.send(message);
        respMessage = replychannel.receive(10_000L);

        assertNotNull(respMessage);
        assertEquals(1, respMessage.getPayload());
        cachingChannel.send(message);
        respMessage = replychannel.receive(10_000L);

        assertNotNull(respMessage);
        assertEquals(1, respMessage.getPayload());

        Thread.sleep(1_000);
        cachingChannel.send(message);
        Message<?> message1 = replychannel.receive(10_000L);
        assertNotNull(message1);
        assertEquals(2, message1.getPayload());

    }

    @ParameterizedTest
    @MethodSource("parseTimeUnitParameters")
    void parseTimeUnit(TimeUnit unit, String expected){
        assertEquals(expected, CacheRequestHandlerAdviceBuilder.parseTimeUnit(unit));

    }

    private static Stream<Arguments> parseTimeUnitParameters(){
        return Stream.of(Arguments.of(TimeUnit.MINUTES, "m"),
                Arguments.of(TimeUnit.SECONDS, "s"),
                Arguments.of(TimeUnit.HOURS, "h"),
                Arguments.of(TimeUnit.DAYS, "d"));
    }

    @Test
    void parseTimeUnitFail(){
        Assertions.assertThatThrownBy(()->CacheRequestHandlerAdviceBuilder.parseTimeUnit(TimeUnit.MICROSECONDS))
        .isInstanceOf(IllegalArgumentException.class);

    }
}