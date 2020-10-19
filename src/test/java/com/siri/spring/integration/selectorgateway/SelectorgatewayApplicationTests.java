package com.siri.spring.integration.selectorgateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SelectorgatewayApplicationTests {

  @Qualifier("selectingGateway.input")
  @Autowired
  MessageChannel selectorChannel;

  @Qualifier("selectingGatewayInterceptor.input")
  @Autowired
  MessageChannel selectorAdviceChannel;

  @Qualifier("loopingGateway.input")
  @Autowired
  MessageChannel loopingChannel;

  @Qualifier("cachingGateway.input")
  @Autowired
  MessageChannel cachingChannel;

  @Qualifier("cachingGatewayTest1.input")
  @Autowired
  MessageChannel cachingChannel1;

  @Autowired
  IntegrationFlowContext context;

  @Test
  void contextLoads() {}

  @Test
  void gatewayTest() {



    QueueChannel replychannel = new QueueChannel();
    Message message =
        MessageBuilder.withPayload("test")
            .setHeader("filter", "true")
            .setReplyChannel(replychannel)
            .build();

    selectorChannel.send(message);
    Message<?> respMessage = replychannel.receive(10_000L);

    assertEquals("TEST", respMessage.getPayload());
  }

  @Test
  void gatewayFilterTest() {

    QueueChannel replychannel = new QueueChannel();
    Message message = MessageBuilder.withPayload("test")
			.setHeader("filter", "false")
			.setReplyChannel(replychannel).build();

	  selectorChannel.send(message);
    Message<?> respMessage = replychannel.receive(10_000L);

    assertEquals("test", respMessage.getPayload());
  }

  @Test
  void gatewayAdviceTest() {

    QueueChannel replychannel = new QueueChannel();
    Message message =
        MessageBuilder.withPayload("test")
            .setHeader("filter", true)
            .setReplyChannel(replychannel)
            .build();

    selectorAdviceChannel.send(message);
    Message<?> respMessage = replychannel.receive(10_000L);

    assertEquals("TEST", respMessage.getPayload());
  }



  @Test
  void gatewayAdviceFilterTest() {

    QueueChannel replychannel = new QueueChannel();
    Message message = MessageBuilder.withPayload("test").setReplyChannel(replychannel).build();

    selectorAdviceChannel.send(message);
    Message<?> respMessage = replychannel.receive(10_000L);

    assertNotNull(respMessage);
    assertEquals("test", respMessage.getPayload());
  }


}
