package com.example.datausersservice;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;

public class UsersServiceConfiguration {

    @Bean
    public IRule ribbonRule() {
        return new RandomRule();
    }
}
