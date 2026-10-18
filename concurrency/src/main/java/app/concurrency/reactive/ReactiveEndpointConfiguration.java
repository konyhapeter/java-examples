package app.concurrency.reactive;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
@Import(WebFluxIoController.class)
public class ReactiveEndpointConfiguration {
}
