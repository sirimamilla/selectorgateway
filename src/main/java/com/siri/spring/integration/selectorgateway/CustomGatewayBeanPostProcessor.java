package com.siri.spring.integration.selectorgateway;

import org.aopalliance.aop.Advice;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.gateway.GatewayMessageHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@ConfigurationProperties(prefix = "com.citi.api.custom.gateway.timeout")
public class CustomGatewayBeanPostProcessor implements BeanPostProcessor {


  List<GatewayTimeoutConfig> config=new ArrayList<>();

  public List<GatewayTimeoutConfig> getConfig() {
    return config;
  }

  public void setConfig(List<GatewayTimeoutConfig> config) {
    this.config = config;
  }

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) {

    if (bean instanceof GatewayMessageHandler ) {

      setGatewayTimeoutCountingRequestHandler(bean, beanName);

      updateTimeout((GatewayMessageHandler) bean, beanName);
    }

    return bean;
  }

  private void setGatewayTimeoutCountingRequestHandler(Object bean, String beanName) {
    List<Advice> adviceChain = (List< Advice>)new DirectFieldAccessor(bean).getPropertyValue("adviceChain");

    boolean b1 = null!=adviceChain && adviceChain.stream().noneMatch(GatewayTimeoutCountingRequestHandler.class::isInstance);
    if(b1){
      adviceChain.add(new GatewayTimeoutCountingRequestHandler(beanName));
    }
  }

  private void updateTimeout(GatewayMessageHandler bean, String beanName) {
    Optional<Long> first = config.stream()
            .filter(c -> beanName.equals(c.getName()))
            .map(GatewayTimeoutConfig::getTimeout)
            .findFirst();
    first.ifPresent(bean::setReplyTimeout);
  }

}
