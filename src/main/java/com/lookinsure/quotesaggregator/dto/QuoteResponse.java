package com.lookinsure.quotesaggregator.dto;

import com.lookinsure.quotesaggregator.entity.CoverageType;

import java.math.BigDecimal;

public record QuoteResponse(
        Long id,
        CoverageType coverageType,
        BigDecimal price,
        String providerName
) {}
