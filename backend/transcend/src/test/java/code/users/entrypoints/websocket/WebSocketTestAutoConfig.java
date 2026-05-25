package code.users.entrypoints.websocket;

import static code.users.entrypoints.websocket.util.WebSocketSecurityUtil.createStompClient;

import code.users.domain.model.UserFixtures;
import code.users.entrypoints.websocket.util.WebSocketSecurityUtil;
import code.users.infrastructure.security.JwtTokenService;
import code.users.infrastructure.security.config.SocketJwtInterceptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Configuration
@ImportAutoConfiguration({
  ServletWebServerFactoryAutoConfiguration.class,
  DispatcherServletAutoConfiguration.class,
  WebMvcAutoConfiguration.class,
  JacksonAutoConfiguration.class,
  HttpMessageConvertersAutoConfiguration.class,
  WebSocketMessagingAutoConfiguration.class,
  WebSocketServletAutoConfiguration.class
})
@Import({WebSocketConfiguration.class, SocketJwtInterceptor.class})
public class WebSocketTestAutoConfig {

  protected WebSocketStompClient stompClient;
  protected StompSession session;

  @MockitoBean private JwtTokenService jwtTokenService;
  @MockitoBean private UserDetailsService userDetailsService;

  @BeforeEach
  void setUp() {
    this.stompClient = createStompClient();
    this.stompClient.setMessageConverter(new MappingJackson2MessageConverter());

    WebSocketSecurityUtil.mockAuth(
        jwtTokenService,
        userDetailsService,
        UserFixtures.TOKEN_FIXTURE,
        UserFixtures.ID_FIXTURE,
        UserFixtures.PASSWORD_FIXTURE);
  }

  @AfterEach
  void tearDown() {
    if (session != null && session.isConnected()) {
      session.disconnect();
    }
  }
}
