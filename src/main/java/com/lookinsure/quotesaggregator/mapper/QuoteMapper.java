package com.lookinsure.quotesaggregator.mapper;

import com.lookinsure.quotesaggregator.dto.QuoteRequest;
import com.lookinsure.quotesaggregator.dto.QuoteResponse;
import com.lookinsure.quotesaggregator.entity.Provider;
import com.lookinsure.quotesaggregator.entity.Quote;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface QuoteMapper {

    @Mapping(target = "providerName", source = "provider.name")
    QuoteResponse toResponse(Quote quote);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "provider", source = "provider")
    Quote toEntity(QuoteRequest request, Provider provider);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "provider", source = "provider")
    void updateEntity(@MappingTarget Quote existingQuote, QuoteRequest request, Provider provider);
}
