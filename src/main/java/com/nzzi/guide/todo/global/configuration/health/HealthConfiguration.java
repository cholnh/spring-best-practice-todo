package com.nzzi.guide.todo.global.configuration.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import java.util.concurrent.atomic.AtomicReference;

@Configuration
public class HealthConfiguration {

    @Bean
    @Scope("singleton")
    @Primary
    public AtomicReference<Health> healthAtomicReference() {
        return new AtomicReference<>(Health.up().build());
    }

}
