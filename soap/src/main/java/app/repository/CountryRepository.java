package app.repository;

import com.baeldung.springsoap.gen.Country;

public interface CountryRepository {

    Country findCountry(String name);
}
