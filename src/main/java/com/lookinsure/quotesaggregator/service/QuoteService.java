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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final ProviderRepository providerRepository;
    private final QuoteMapper quoteMapper;

    @Transactional
    public QuoteResponse createQuote(QuoteRequest request) {
        Provider provider = providerRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + request.providerId()));

        Quote quote = quoteMapper.toEntity(request, provider);
        Quote savedQuote = quoteRepository.save(quote);

        return quoteMapper.toResponse(savedQuote);
    }

    @Transactional
    public QuoteResponse updateQuote(Long id, QuoteRequest request) {
        Quote existingQuote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with ID: " + id));

        Provider provider = providerRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found with ID: " + request.providerId()));

        quoteMapper.updateEntity(existingQuote, request, provider);

        Quote updatedQuote = quoteRepository.save(existingQuote);
        return quoteMapper.toResponse(updatedQuote);
    }

    @Transactional
    public void deleteQuote(Long id) {
        if (!quoteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Quote not found with ID: " + id);
        }
        quoteRepository.deleteById(id);
    }
}
