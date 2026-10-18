package app.concurrency.virtualthreads;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
@Import(VirtualThreadIoController.class)
public class VirtualThreadEndpointConfiguration {
}
