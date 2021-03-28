package com.bortman_corparation.demo.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class ExchangeRateDto {

    private final String currency;
    private final BigDecimal value;


}
