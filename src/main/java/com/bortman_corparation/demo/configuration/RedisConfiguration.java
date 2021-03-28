package com.bortman_corparation.demo.configuration;


import com.bortman_corparation.demo.entity.ExchangeRate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {


    @Bean
    ReactiveRedisOperations<String, ExchangeRate>
    redisOperations(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<ExchangeRate> serializer = new Jackson2JsonRedisSerializer<>(ExchangeRate.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, ExchangeRate> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
        RedisSerializationContext<String, ExchangeRate> context = builder.value(serializer).build();
        return new ReactiveRedisTemplate<>(factory, context);
    }

}
