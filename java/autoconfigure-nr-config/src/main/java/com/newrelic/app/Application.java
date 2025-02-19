package com.newrelic.app;

import io.opentelemetry.instrumentation.log4j.appender.v2_16.OpenTelemetryAppender;
import io.opentelemetry.instrumentation.runtimemetrics.GarbageCollector;
import io.opentelemetry.instrumentation.runtimemetrics.MemoryPools;
import io.opentelemetry.instrumentation.spring.webmvc.SpringWebMvcTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.autoconfigure.AutoConfiguredOpenTelemetrySdk;
import javax.servlet.Filter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

  private static final AutoConfiguredOpenTelemetrySdk autoConfiguredOpenTelemetrySdk =
      AutoConfiguredOpenTelemetrySdk.initialize();

  public static void main(String[] args) {
    OpenTelemetrySdk openTelemetrySdk = autoConfiguredOpenTelemetrySdk.getOpenTelemetrySdk();

    // Initialize Log4j2 appender
    OpenTelemetryAppender.setSdkLogEmitterProvider(openTelemetrySdk.getSdkLogEmitterProvider());

    // Register runtime metrics instrumentation
    MemoryPools.registerObservers(openTelemetrySdk);
    GarbageCollector.registerObservers(openTelemetrySdk);

    SpringApplication.run(Application.class, args);
  }

  @Bean
  public OpenTelemetrySdk openTelemetrySdk() {
    return autoConfiguredOpenTelemetrySdk.getOpenTelemetrySdk();
  }

  /** Add Spring WebMVC instrumentation by registering tracing filter. */
  @Bean
  public Filter webMvcTracingFilter(OpenTelemetrySdk openTelemetrySdk) {
    return SpringWebMvcTelemetry.create(openTelemetrySdk).newServletFilter();
  }
}
