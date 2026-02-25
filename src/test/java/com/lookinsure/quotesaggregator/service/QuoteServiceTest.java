package com.lookinsure.quotesaggregator.service;

import com.lookinsure.quotesaggregator.dto.QuoteRequest;
import com.lookinsure.quotesaggregator.dto.QuoteResponse;
import com.lookinsure.quotesaggregator.entity.CoverageType;
import com.lookinsure.quotesaggregator.entity.Provider;
import com.lookinsure.quotesaggregator.entity.Quote;
import com.lookinsure.quotesaggregator.exception.ResourceNotFoundException;
import com.lookinsure.quotesaggregator.mapper.QuoteMapper;
import com.lookinsure.quotesaggregator.repository.ProviderRepository;
import com.lookinsure.quotesaggregator.repository.QuoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private QuoteMapper quoteMapper;

    @InjectMocks
    private QuoteService quoteService;

    @Test
    void createQuote_Success() {
        Long providerId = 1L;
        QuoteRequest request = new QuoteRequest(CoverageType.CAR, BigDecimal.valueOf(100), providerId);
        Provider provider = new Provider(providerId, "Test Provider");
        Quote quote = new Quote(null, CoverageType.CAR, BigDecimal.valueOf(100), provider);
        Quote savedQuote = new Quote(1L, CoverageType.CAR, BigDecimal.valueOf(100), provider);
        QuoteResponse expectedResponse = new QuoteResponse(1L, CoverageType.CAR, BigDecimal.valueOf(100), "Test Provider");

        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(quoteMapper.toEntity(request, provider)).thenReturn(quote);
        when(quoteRepository.save(quote)).thenReturn(savedQuote);
        when(quoteMapper.toResponse(savedQuote)).thenReturn(expectedResponse);

        QuoteResponse actualResponse = quoteService.createQuote(request);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.id(), actualResponse.id());
        verify(quoteRepository).save(quote);
    }

    @Test
    void createQuote_ProviderNotFound() {
        QuoteRequest request = new QuoteRequest(CoverageType.CAR, BigDecimal.valueOf(100), 99L);
        when(providerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> quoteService.createQuote(request));
        verify(quoteRepository, never()).save(any());
    }

    @Test
    void getQuoteById_Success() {
        Long quoteId = 1L;
        Quote quote = new Quote(quoteId, CoverageType.HEALTH, BigDecimal.TEN, new Provider());
        QuoteResponse expectedResponse = new QuoteResponse(quoteId, CoverageType.HEALTH, BigDecimal.TEN, "Provider");

        when(quoteRepository.findById(quoteId)).thenReturn(Optional.of(quote));
        when(quoteMapper.toResponse(quote)).thenReturn(expectedResponse);

        QuoteResponse result = quoteService.getQuoteById(quoteId);

        assertEquals(expectedResponse, result);
    }

    @Test
    void getAllQuotes_Success() {
        Pageable pageable = PageRequest.of(0, 10);

        Quote quote1 = new Quote(1L, CoverageType.CAR, BigDecimal.TEN, new Provider(1L, "P1"));
        Quote quote2 = new Quote(2L, CoverageType.HEALTH, BigDecimal.valueOf(20), new Provider(2L, "P2"));
        Page<Quote> quotePage = new PageImpl<>(List.of(quote1, quote2));

        when(quoteRepository.findAll(pageable)).thenReturn(quotePage);
        when(quoteMapper.toResponse(any(Quote.class))).thenReturn(new QuoteResponse(1L, CoverageType.CAR, BigDecimal.TEN, "P1"));

        Page<QuoteResponse> result = quoteService.getAllQuotes(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(quoteRepository).findAll(pageable);
    }


    @Test
    void updateQuote_Success() {
        Long quoteId = 1L;
        Long providerId = 2L;
        QuoteRequest request = new QuoteRequest(CoverageType.PET, BigDecimal.valueOf(50), providerId);

        Quote existingQuote = new Quote(quoteId, CoverageType.CAR, BigDecimal.valueOf(100), new Provider());
        Provider provider = new Provider(providerId, "Provider");
        Quote updatedQuote = new Quote(quoteId, CoverageType.PET, BigDecimal.valueOf(50), provider);
        QuoteResponse response = new QuoteResponse(quoteId, CoverageType.PET, BigDecimal.valueOf(50), "Provider");

        when(quoteRepository.findById(quoteId)).thenReturn(Optional.of(existingQuote));
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(provider));
        when(quoteRepository.save(existingQuote)).thenReturn(updatedQuote);
        when(quoteMapper.toResponse(updatedQuote)).thenReturn(response);

        QuoteResponse result = quoteService.updateQuote(quoteId, request);

        assertEquals(CoverageType.PET, result.coverageType());
        verify(quoteMapper).updateEntity(existingQuote, request, provider);
    }

    @Test
    void updateQuote_QuoteNotFound() {
        QuoteRequest request = new QuoteRequest(CoverageType.PET, BigDecimal.TEN, 1L);
        when(quoteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> quoteService.updateQuote(1L, request));
    }

    @Test
    void deleteQuote_Success() {
        Long quoteId = 1L;
        when(quoteRepository.existsById(quoteId)).thenReturn(true);

        quoteService.deleteQuote(quoteId);

        verify(quoteRepository).deleteById(quoteId);
    }

    @Test
    void deleteQuote_NotFound() {
        Long quoteId = 99L;
        when(quoteRepository.existsById(quoteId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> quoteService.deleteQuote(quoteId));
        verify(quoteRepository, never()).deleteById(any());
    }

    @Test
    void getAggregatedQuotes_Success() {
        Quote cheap = new Quote(1L, CoverageType.CAR, BigDecimal.valueOf(10), new Provider());
        Quote expensive = new Quote(2L, CoverageType.CAR, BigDecimal.valueOf(20), new Provider());
        List<Quote> sortedList = List.of(cheap, expensive);

        when(quoteRepository.findAllByOrderByPriceAsc()).thenReturn(sortedList);
        when(quoteMapper.toResponse(any(Quote.class))).thenReturn(new QuoteResponse(1L, CoverageType.CAR, BigDecimal.valueOf(10), "Provider"));

        List<QuoteResponse> result = quoteService.getAggregatedQuotes();

        assertEquals(2, result.size());
        verify(quoteRepository).findAllByOrderByPriceAsc();
    }

}