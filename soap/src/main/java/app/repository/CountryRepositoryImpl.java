package app.repository;

import com.baeldung.springsoap.gen.Country;
import com.baeldung.springsoap.gen.Currency;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CountryRepositoryImpl implements CountryRepository  {

    private static final Map<String, Country> countries = new HashMap<>();

    @PostConstruct
    public void initData() {

        Country greatBritain = new Country();
        greatBritain.setCapital("London");
        greatBritain.setName("Great Britain");
        greatBritain.setCurrency(Currency.GBP);
        greatBritain.setPopulation(100);

        countries.put("Great Britain", greatBritain);

        Country germany = new Country();
        germany.setCapital("Berlin");
        germany.setName("Germany");
        germany.setCurrency(Currency.EUR);
        germany.setPopulation(200);

        countries.put("Germany", germany);

        Country poland = new Country();
        poland.setCapital("Warsaw");
        poland.setName("Poland");
        poland.setCurrency(Currency.PLN);
        poland.setPopulation(50);

        countries.put("Poland", poland);
    }

    public Country findCountry(String name) {
        return countries.get(name);
    }
}
