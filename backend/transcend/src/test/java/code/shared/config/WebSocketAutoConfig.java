package code.shared.config;

import code.users.entrypoints.websocket.UserWebSocketConfig;
import code.users.infrastructure.security.config.SocketJwtInterceptor;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

@TestConfiguration
@OverrideAutoConfiguration(enabled = false)
@ImportAutoConfiguration({
  ServletWebServerFactoryAutoConfiguration.class,
  DispatcherServletAutoConfiguration.class,
  WebMvcAutoConfiguration.class,
  JacksonAutoConfiguration.class,
  HttpMessageConvertersAutoConfiguration.class,
  WebSocketMessagingAutoConfiguration.class,
  WebSocketServletAutoConfiguration.class,
  RedisAutoConfiguration.class,
  RedissonAutoConfigurationV2.class,
  ConfigurationPropertiesAutoConfiguration.class,
  PropertyPlaceholderAutoConfiguration.class,
})
@Import({UserWebSocketConfig.class, SocketJwtInterceptor.class})
public class WebSocketAutoConfig {}
