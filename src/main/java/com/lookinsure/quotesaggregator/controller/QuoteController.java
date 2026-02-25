package com.lookinsure.quotesaggregator.controller;

import com.lookinsure.quotesaggregator.dto.QuoteRequest;
import com.lookinsure.quotesaggregator.dto.QuoteResponse;
import com.lookinsure.quotesaggregator.service.QuoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<QuoteResponse> createQuote(@Valid @RequestBody QuoteRequest request) {
        QuoteResponse response = quoteService.createQuote(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuoteResponse> getQuote(@PathVariable Long id) {
        return ResponseEntity.ok(quoteService.getQuoteById(id));
    }

    @GetMapping
    public ResponseEntity<Page<QuoteResponse>> getAllQuotes(
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(quoteService.getAllQuotes(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuoteResponse> updateQuote(
            @PathVariable Long id,
            @Valid @RequestBody QuoteRequest request) {
        return ResponseEntity.ok(quoteService.updateQuote(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        quoteService.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/aggregate")
    public ResponseEntity<List<QuoteResponse>> getAggregatedQuotes() {
        return ResponseEntity.ok(quoteService.getAggregatedQuotes());
    }
}
