package com.lookinsure.quotesaggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class LookinsureQuotesAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(LookinsureQuotesAggregatorApplication.class, args);
    }

}
