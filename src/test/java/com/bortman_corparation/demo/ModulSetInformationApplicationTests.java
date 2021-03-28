package com.bortman_corparation.demo;

import com.bortman_corparation.demo.entity.ExchangeRate;
import com.bortman_corparation.demo.repository.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisOperations;

import java.math.BigDecimal;
import java.sql.Timestamp;

@SpringBootTest
class ModulSetInformationApplicationTests {

    @Autowired
    ReactiveRedisOperations<String, ExchangeRate> redisOperations;
    @Autowired
    ExchangeRateRepository exchangeRateRepository;

    @Test
    void contextLoads() {
    }


}
