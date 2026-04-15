package code;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.modulith.Modulithic;

import java.util.TimeZone;

@Modulithic
@SpringBootApplication
class TranscendApp extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(TranscendApp.class, args);
  }

  @PostConstruct
  public void setUtcTimeZone() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
}