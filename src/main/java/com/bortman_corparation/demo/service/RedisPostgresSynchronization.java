package com.bortman_corparation.demo.service;

import com.bortman_corparation.demo.entity.ExchangeRate;
import com.bortman_corparation.demo.repository.ExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.List;

@Service
public class RedisPostgresSynchronization {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ReactiveRedisOperations<String, ExchangeRate> redisOperations;

    @Autowired
    public RedisPostgresSynchronization(ExchangeRateRepository exchangeRateRepository, ReactiveRedisOperations<String, ExchangeRate> redisOperations) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.redisOperations = redisOperations;
    }

    private static final ExchangeRate stubExchangeRate = new ExchangeRate("STUB", new BigDecimal("00.00"), Timestamp.valueOf("1983-07-12 21:30:55.888"));

    public ExchangeRate findByCurrency(String current){
        return exchangeRateRepository.findByCurrency(current) == null ? stubExchangeRate
                :  exchangeRateRepository.findByCurrency(current);
    }

    public  void datSynchronization() {
        List<ExchangeRate> exchangeRatesList = redisOperations.keys("*")
                .flatMap(redisOperations.opsForValue()::get)
                .filter(a -> a.getChangeData()
                        .compareTo(findByCurrency(a.getCurrency()).getChangeData()) > 0)
                .collectList().block(Duration.ofSeconds(2));
        assert exchangeRatesList != null;
        if (!exchangeRatesList.isEmpty()) {
            for (ExchangeRate exchangeRate :exchangeRatesList)  {
                ExchangeRate exchangeRateDB = exchangeRateRepository.findByCurrency(exchangeRate.getCurrency());
                if (exchangeRateDB != null){
                    exchangeRateDB.setValue(exchangeRate.getValue());
                    exchangeRateDB.setChangeData(exchangeRate.getChangeData());
                    exchangeRateRepository.save(exchangeRateDB);
                } else {
                    exchangeRateRepository.save(exchangeRate);
                }
            }
        }
    }
}
