package com.lookinsure.quotesaggregator.config;

import com.lookinsure.quotesaggregator.entity.CoverageType;
import com.lookinsure.quotesaggregator.entity.Provider;
import com.lookinsure.quotesaggregator.entity.Quote;
import com.lookinsure.quotesaggregator.repository.ProviderRepository;
import com.lookinsure.quotesaggregator.repository.QuoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ProviderRepository providerRepository;
    private final QuoteRepository quoteRepository;

    @Override
    public void run(String... args) {
        if (providerRepository.count() == 0) {
            Provider p1 = providerRepository.save(Provider.builder().name("Dubai National Insurance").build());
            Provider p2 = providerRepository.save(Provider.builder().name("Orient Insurance").build());
            Provider p3 = providerRepository.save(Provider.builder().name("RAK Insurance").build());

            quoteRepository.saveAll(List.of(
                    Quote.builder().coverageType(CoverageType.CAR).price(BigDecimal.valueOf(500)).provider(p1).build(),
                    Quote.builder().coverageType(CoverageType.CAR).price(BigDecimal.valueOf(350)).provider(p2).build(),
                    Quote.builder().coverageType(CoverageType.CAR).price(BigDecimal.valueOf(450)).provider(p3).build(),
                    Quote.builder().coverageType(CoverageType.PET).price(BigDecimal.valueOf(150)).provider(p1).build(),
                    Quote.builder().coverageType(CoverageType.HEALTH).price(BigDecimal.valueOf(1200)).provider(p3).build()
            ));

            System.out.println("--- Data Loaded: 3 Providers and 5 Quotes ---");
        }
    }
}
