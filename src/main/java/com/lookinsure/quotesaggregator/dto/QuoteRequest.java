package com.lookinsure.quotesaggregator.dto;

import com.lookinsure.quotesaggregator.entity.CoverageType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record QuoteRequest(
        @NotNull(message = "Coverage Type cannot be null")
        CoverageType coverageType,

        @NotNull(message = "Price cannot be null")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @NotNull(message = "Provider ID is required")
        Long providerId
) {}
