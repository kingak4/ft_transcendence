package code.shared.config;

import static code.shared.util.WebSocketSecurityUtil.createStompClient;

import code.shared.domain.model.WebSocketFixtures;
import code.shared.util.WebSocketSecurityUtil;
import code.users.bootstrap.DefaultAvatarInitializer;
import code.users.infrastructure.persistence.UserJpaRepository;
import code.users.infrastructure.security.JwtTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.concurrent.BlockingQueue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@SpringBootTest
public class WebSocketTest extends EmbeddedRedisTestSupport {

  protected WebSocketStompClient stompClient;
  public static final Duration TIMEOUT = Duration.ofSeconds(20);
  protected StompSession session;

  @MockitoBean private JwtTokenService jwtTokenService;
  @MockitoBean private UserDetailsService userDetailsService;

  @MockitoBean private DefaultAvatarInitializer defaultAvatarInitializer;

  @MockitoBean private UserJpaRepository userJpaRepository;

  @Autowired private ObjectMapper mapper;

  @BeforeEach
  void setUp() {
    this.stompClient = createStompClient();
    MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
    messageConverter.setObjectMapper(mapper);
    this.stompClient.setMessageConverter(messageConverter);

    WebSocketSecurityUtil.mockAuth(
        jwtTokenService,
        userDetailsService,
        WebSocketFixtures.TOKEN_FIXTURE,
        WebSocketFixtures.ID_FIXTURE,
        WebSocketFixtures.PASSWORD_FIXTURE);
  }

  @AfterEach
  void tearDown() {
    if (session != null && session.isConnected()) {
      session.disconnect();
    }
    Mockito.reset(jwtTokenService, userDetailsService, userJpaRepository, defaultAvatarInitializer);
  }

  public record QueueingFrameHandler<T>(Class<T> payloadType, BlockingQueue<T> queue)
      implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders headers) {
      return payloadType;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
      queue.add((T) payload);
    }
  }
}
