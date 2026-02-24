package com.lookinsure.quotesaggregator.repository;

import com.lookinsure.quotesaggregator.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteRepository extends JpaRepository<Quote, Long> {

    List<Quote> findAllByOrderByPriceAsc();

}
