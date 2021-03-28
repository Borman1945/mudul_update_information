package com.bortman_corparation.demo.contoller;

import com.bortman_corparation.demo.entity.ExchangeRate;
import com.bortman_corparation.demo.model.ExchangeRateDto;
import com.bortman_corparation.demo.repository.ExchangeRateRepository;
import com.bortman_corparation.demo.service.ExchangeRateSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class RateController {

    @Autowired
    private ExchangeRateRepository repository;

    private final ExchangeRateSetService rateSetService;

    @Autowired
    public RateController(ExchangeRateSetService rateSetService) {
        this.rateSetService = rateSetService;
    }

    @PostMapping("/new_rate")
    public Mono<ExchangeRate> setNewExchageRate(@RequestBody final ExchangeRateDto rateDto){
        return rateSetService.newExchangeRow(rateDto);
    }

    // пут тут не работает установить в чем проблема
    @PutMapping("/update_rate")
    public void updateExchageRate(@RequestBody final ExchangeRateDto rateDto){
          rateSetService.updateRateValue(rateDto);
    }


}
