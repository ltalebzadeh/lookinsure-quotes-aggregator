package com.lookinsure.quotesaggregator.service;

import com.lookinsure.quotesaggregator.dto.QuoteRequest;
import com.lookinsure.quotesaggregator.dto.QuoteResponse;
import com.lookinsure.quotesaggregator.entity.Provider;
import com.lookinsure.quotesaggregator.entity.Quote;
import com.lookinsure.quotesaggregator.exception.ResourceNotFoundException;
import com.lookinsure.quotesaggregator.mapper.QuoteMapper;
import com.lookinsure.quotesaggregator.repository.ProviderRepository;
import com.lookinsure.quotesaggregator.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final ProviderRepository providerRepository;
    private final QuoteMapper quoteMapper;

    public QuoteResponse createQuote(QuoteRequest request) {
        Provider provider = providerRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + request.providerId()));

        Quote quote = quoteMapper.toEntity(request, provider);
        Quote savedQuote = quoteRepository.save(quote);

        return quoteMapper.toResponse(savedQuote);
    }
}
