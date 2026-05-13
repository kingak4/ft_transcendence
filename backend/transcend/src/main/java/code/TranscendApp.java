package code;

import code.bootstrap.DotEnvInitializer;
import code.bootstrap.ValidProfileInitializer;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.Modulithic;

@Slf4j
@Modulithic
@SpringBootApplication
@ConfigurationPropertiesScan
class TranscendApp extends SpringBootServletInitializer {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(TranscendApp.class);
    app.addInitializers(new DotEnvInitializer(), new ValidProfileInitializer());
    app.run(args);
  }

  @PostConstruct
  public void setUtcTimeZone() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  @EventListener(ApplicationReadyEvent.class)
  public void logDbUrl() {
    log.info("Resolved DB URL: {}", dbUrl);
  }
}