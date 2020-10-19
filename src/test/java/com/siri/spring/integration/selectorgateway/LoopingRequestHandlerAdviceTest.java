package com.siri.spring.integration.selectorgateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class LoopingRequestHandlerAdviceTest {
    @Qualifier("loopingGateway.input")
    @Autowired
    MessageChannel loopingChannel;

    @Test
    void loopingGatewayTest() {

        QueueChannel replychannel = new QueueChannel();
        Message message =
                MessageBuilder.withPayload("test")
                        .setHeader("filter", true)
                        .setReplyChannel(replychannel)
                        .build();

        loopingChannel.send(message);
        Message<?> respMessage = replychannel.receive(10_000L);

        assertThat(respMessage).extracting(Message::getPayload)
                .isInstanceOf(List.class)
                .hasFieldOrPropertyWithValue("size", 4);
        assertThat(((List)respMessage.getPayload())).containsExactly("TEST", "TEST", "TEST", "TEST");
    }

    @Test
    void loopingGatewayFailTest() {

        QueueChannel replychannel = new QueueChannel();
        Message message =
                MessageBuilder.withPayload("TEST1")
                        .setHeader("filter", true)
                        .setReplyChannel(replychannel)
                        .build();

        assertThatThrownBy(()->loopingChannel.send(message))
                .getRootCause()
                .hasMessage("Intentional Error thrown to break loop");
    }

}