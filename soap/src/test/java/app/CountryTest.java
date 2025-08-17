package app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CountryTest {


    private static final String COMMAND = "curl --header \"content-type: text/xml\" --data @src/test/resources/polandRequest.xml http://localhost:8090/ws";

    Logger logger = LoggerFactory.getLogger(CountryTest.class);

    @Test
    public void testPoland() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        ProcessBuilder processBuilder = new ProcessBuilder(COMMAND.split(" "));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String result = reader.lines().collect(Collectors.joining("\n"));
            result = "\n" + result;
            logger.info(result);
        }

        int expectedExitCode = 0;
        int exitCode = process.waitFor();
        Assertions.assertEquals(expectedExitCode, exitCode, "Exitcode should be " + expectedExitCode);
    }
}
