package com.siri.spring.integration.selectorgateway;

import lombok.Data;

@Data
public class GatewayTimeoutConfig {
    String name;
    Long timeout;
}
