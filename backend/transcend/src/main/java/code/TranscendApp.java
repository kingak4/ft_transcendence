package code;

import code.bootstrap.UserFixtureInitializer;
import code.bootstrap.ValidProfileInitializer;
import code.users.config.DotenvApplicationContextInitializer;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.modulith.Modulithic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@Modulithic
@SpringBootApplication
@ConfigurationPropertiesScan
class TranscendApp extends SpringBootServletInitializer {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(TranscendApp.class);
    app.addInitializers(new ValidProfileInitializer());
    app.addInitializers(new DotenvApplicationContextInitializer());
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