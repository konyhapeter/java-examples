package app;

import com.baeldung.springsoap.gen.Currency;
import com.baeldung.springsoap.gen.GetCountryRequest;
import com.baeldung.springsoap.gen.GetCountryResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ws.client.core.WebServiceTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CountryTest {

    @Autowired
    private WebServiceTemplate webServiceTemplate;

    @Test
    public void testPoland() {
        //given
        GetCountryRequest request = new GetCountryRequest();
        request.setName("Poland");

        String url = "http://localhost:8090/ws";

        //when
        GetCountryResponse response = (GetCountryResponse) webServiceTemplate
                .marshalSendAndReceive(url, request);

        //then
        String capital = "Warsaw";
        Currency currency = Currency.PLN;
        int population = 50;

        assertEquals(capital, response.getCountry().getCapital(), "Capital should be " + capital);
        assertEquals(currency, response.getCountry().getCurrency(), "Currency should be " + currency);
        assertEquals(population, response.getCountry().getPopulation(), "Population should be " + population);

    }
}
