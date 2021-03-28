package com.bortman_corparation.demo.service;

import com.bortman_corparation.demo.component.ConnectMonitoring;
import com.bortman_corparation.demo.entity.ExchangeRate;
import com.bortman_corparation.demo.model.ExchangeRateDto;
import com.bortman_corparation.demo.repository.ExchangeRateRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.CannotCreateTransactionException;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import java.sql.Timestamp;

@Service
public class ExchangeRateSetService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ReactiveRedisOperations<String, ExchangeRate> redisOperations;
    private final ConnectMonitoring monitoring;

    public ExchangeRateSetService(ExchangeRateRepository exchangeRateRepository,
                                  ReactiveRedisOperations<String, ExchangeRate> redisOperations, ConnectMonitoring monitoring) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.redisOperations = redisOperations;
        this.monitoring = monitoring;
    }

    public void updateRateValue(ExchangeRateDto rateDto) {
        Mono<ExchangeRate> exchangeRateMono = getRowRedisUpdate(rateDto.getCurrency());
        exchangeRateMono.flatMap(a -> {
            try {
                if (a.getId() == 0) {
                    ExchangeRate rateDB = exchangeRateRepository.findByCurrency(a.getCurrency());
                    if (rateDB != null) {
                        a.setId(rateDB.getId());
                    }
                }
            } catch (DataAccessException e) {
                e.printStackTrace();
                a.setValue(rateDto.getValue());
                a.setChangeData(new Timestamp(System.currentTimeMillis()));
                return Mono.justOrEmpty(getUpdateExchDataFromRedis(a));
            }
            a.setValue(rateDto.getValue());
            a.setChangeData(new Timestamp(System.currentTimeMillis()));
            return updatePostrgreAndRedisDb(a);
        }).subscribe();
    }

    public Mono<ExchangeRate> newExchangeRow(ExchangeRateDto exchangeRateDto) {
        ExchangeRate newExchangeRate = new ExchangeRate(exchangeRateDto.getCurrency(), exchangeRateDto.getValue(), new Timestamp(System.currentTimeMillis()));
        return updatePostrgreAndRedisDb(newExchangeRate);
    }

    private Mono<ExchangeRate> updatePostrgreAndRedisDb(ExchangeRate exchangeRate) {
        if (monitoring.isConnect()) {
            return Mono.justOrEmpty(getUpdateExchDataFromPostgres(getUpdateExchDataFromRedis(exchangeRate)));
        } else {
            return Mono.justOrEmpty(getUpdateExchDataFromRedis(exchangeRate));
        }
    }


    private Mono<ExchangeRate> getRowRedisUpdate(String currency) {
        return redisOperations.opsForValue().get(currency);
    }

    public ExchangeRate getUpdateExchDataFromRedis(ExchangeRate updateRate) {
        redisOperations.opsForValue().set(updateRate.getCurrency(), updateRate).subscribe();
        return updateRate;
    }

    //@Transactional
    private ExchangeRate getUpdateExchDataFromPostgres(ExchangeRate exchangeRate) {
        try {
            return exchangeRateRepository.save(exchangeRate);
        } catch (CannotCreateTransactionException e) {
            e.printStackTrace();
            return exchangeRate;
        }
    }


}
