package com.lookinsure.quotesaggregator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lookinsure.quotesaggregator.dto.QuoteRequest;
import com.lookinsure.quotesaggregator.dto.QuoteResponse;
import com.lookinsure.quotesaggregator.entity.CoverageType;
import com.lookinsure.quotesaggregator.exception.ResourceNotFoundException;
import com.lookinsure.quotesaggregator.service.QuoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuoteController.class)
class QuoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuoteService quoteService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createQuote_Success() throws Exception {
        QuoteRequest request = new QuoteRequest(CoverageType.CAR, BigDecimal.valueOf(100), 1L);
        QuoteResponse response = new QuoteResponse(1L, CoverageType.CAR, BigDecimal.valueOf(100), "Provider");

        when(quoteService.createQuote(any(QuoteRequest.class))).thenReturn(response);

        mockMvc.perform(post("/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.providerName").value("Provider"));
    }

    @Test
    void createQuote_ValidationFailure() throws Exception {
        QuoteRequest invalidRequest = new QuoteRequest(CoverageType.CAR, BigDecimal.valueOf(-10), 1L);

        mockMvc.perform(post("/quotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    void getQuoteById_Success() throws Exception {
        QuoteResponse response = new QuoteResponse(1L, CoverageType.HEALTH, BigDecimal.valueOf(200), "Provider");

        when(quoteService.getQuoteById(1L)).thenReturn(response);

        mockMvc.perform(get("/quotes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coverageType").value("HEALTH"));
    }

    @Test
    void getQuoteById_NotFound() throws Exception {
        when(quoteService.getQuoteById(99L)).thenThrow(new ResourceNotFoundException("Not found"));

        mockMvc.perform(get("/quotes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void getAllQuotes_Success() throws Exception {
        QuoteResponse q1 = new QuoteResponse(1L, CoverageType.CAR, BigDecimal.valueOf(10), "Provider 1");
        QuoteResponse q2 = new QuoteResponse(2L, CoverageType.CAR, BigDecimal.valueOf(20), "Provider 2");
        Page<QuoteResponse> page = new PageImpl<>(List.of(q1, q2));

        when(quoteService.getAllQuotes(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/quotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].providerName").value("Provider 1"));
    }

    @Test
    void updateQuote_Success() throws Exception {
        QuoteRequest request = new QuoteRequest(CoverageType.PET, BigDecimal.valueOf(50), 1L);
        QuoteResponse response = new QuoteResponse(1L, CoverageType.PET, BigDecimal.valueOf(50), "Provider");

        when(quoteService.updateQuote(eq(1L), any(QuoteRequest.class))).thenReturn(response);

        mockMvc.perform(put("/quotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(50));
    }

    @Test
    void deleteQuote_Success() throws Exception {
        mockMvc.perform(delete("/quotes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAggregatedQuotes_Success() throws Exception {
        QuoteResponse q1 = new QuoteResponse(1L, CoverageType.CAR, BigDecimal.valueOf(10), "Provider 1");
        QuoteResponse q2 = new QuoteResponse(2L, CoverageType.CAR, BigDecimal.valueOf(20), "Provider 2");

        when(quoteService.getAggregatedQuotes()).thenReturn(List.of(q1, q2));

        mockMvc.perform(get("/quotes/aggregate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").value(10))
                .andExpect(jsonPath("$[1].price").value(20));
    }

}