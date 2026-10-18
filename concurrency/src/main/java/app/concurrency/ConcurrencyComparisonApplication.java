package app.concurrency;

import app.concurrency.reactive.ReactiveEndpointConfiguration;
import app.concurrency.virtualthreads.VirtualThreadEndpointConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.web.reactive.context.ReactiveWebServerApplicationContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

public class ConcurrencyComparisonApplication {

    public static void main(String[] args) {
        int virtualThreadsPort = Integer.getInteger("comparison.virtual-threads.port", 8081);
        int webFluxPort = Integer.getInteger("comparison.webflux.port", 8082);

        ConfigurableApplicationContext virtualThreads = new SpringApplicationBuilder(
                VirtualThreadEndpointConfiguration.class)
                .web(WebApplicationType.SERVLET)
                .properties(
                        "server.address=127.0.0.1",
                        "server.port=" + virtualThreadsPort,
                        "spring.threads.virtual.enabled=true")
                .run(args);

        ConfigurableApplicationContext webFlux = new SpringApplicationBuilder(
                ReactiveEndpointConfiguration.class)
                .web(WebApplicationType.REACTIVE)
                .properties(
                        "server.address=127.0.0.1",
                        "server.port=" + webFluxPort)
                .run(args);

        int actualVirtualThreadsPort =
                ((ServletWebServerApplicationContext) virtualThreads).getWebServer().getPort();
        int actualWebFluxPort =
                ((ReactiveWebServerApplicationContext) webFlux).getWebServer().getPort();
        System.out.printf(
                "Virtual-thread endpoint: http://localhost:%d/io?delayMs=100%n",
                actualVirtualThreadsPort);
        System.out.printf(
                "WebFlux endpoint: http://localhost:%d/io?delayMs=100%n",
                actualWebFluxPort);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            webFlux.close();
            virtualThreads.close();
        }, "comparison-shutdown"));
    }
}
