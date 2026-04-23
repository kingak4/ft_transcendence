package code;

import code.bootstrap.ValidProfileInitializer;
import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.modulith.Modulithic;

@Modulithic
@SpringBootApplication
@ConfigurationPropertiesScan
class TranscendApp extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(TranscendApp.class);
    app.addInitializers(new ValidProfileInitializer());
    app.run(args);
  }

  @PostConstruct
  public void setUtcTimeZone() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
}