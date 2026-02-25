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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional(readOnly = true)
    public QuoteResponse getQuoteById(Long id) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quote not found with ID: " + id));
        return quoteMapper.toResponse(quote);
    }

    @Transactional(readOnly = true)
    public Page<QuoteResponse> getAllQuotes(Pageable pageable) {
        return quoteRepository.findAll(pageable)
                .map(quoteMapper::toResponse);
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

    @Transactional(readOnly = true)
    public List<QuoteResponse> getAggregatedQuotes() {
        List<Quote> sortedQuotes = quoteRepository.findAllByOrderByPriceAsc();

        return sortedQuotes.stream()
                .map(quoteMapper::toResponse)
                .toList();
    }
}
