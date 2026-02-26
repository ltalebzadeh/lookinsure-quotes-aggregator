package com.lookinsure.quotesaggregator.service;

import com.lookinsure.quotesaggregator.dto.QuoteRequest;
import com.lookinsure.quotesaggregator.entity.CoverageType;
import com.lookinsure.quotesaggregator.entity.Provider;
import com.lookinsure.quotesaggregator.repository.ProviderRepository;
import com.lookinsure.quotesaggregator.repository.QuoteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class QuoteCachingIntegrationTest {

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private CacheManager cacheManager;

    @MockitoSpyBean
    private QuoteRepository quoteRepository;

    @BeforeEach
    void setup() {
        tearDown();
        providerRepository.save(new Provider(null, "Test Provider"));
    }

    @AfterEach
    void tearDown() {
        reset(quoteRepository);
        quoteRepository.deleteAll();
        providerRepository.deleteAll();
        cacheManager.getCacheNames()
                .forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @Test
    void testAggregationCache_Hit_And_Evict() {
        // First Call hit the database
        quoteService.getAggregatedQuotes();
        verify(quoteRepository, times(1)).findAllByOrderByPriceAsc();

        // Second Call hit the cache
        quoteService.getAggregatedQuotes();
        verify(quoteRepository, times(1)).findAllByOrderByPriceAsc();

        // Trigger Eviction: Add a new Quote
        Provider p = providerRepository.findAll().get(0);
        QuoteRequest request = new QuoteRequest(CoverageType.CAR, BigDecimal.TEN, p.getId());
        quoteService.createQuote(request);

        // Third Call hit the database
        quoteService.getAggregatedQuotes();
        verify(quoteRepository, times(2)).findAllByOrderByPriceAsc();
    }
}
