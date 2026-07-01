package code.chat.entrypoints.websocket;

import code.chat.ports.in.ManageMessagesUseCase;
import code.shared.config.WebSocketAutoConfig;
import code.shared.config.WebSocketTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = {WebSocketAutoConfig.class, ChatWebSocketController.class})
@Slf4j
class MessageDeleteTest extends WebSocketTest {

  @LocalServerPort private int port;
  @MockitoBean private ManageMessagesUseCase manageMessagesUseCase;
}
