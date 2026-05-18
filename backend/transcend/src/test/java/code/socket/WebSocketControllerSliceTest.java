package code.socket;

import code.sockets.ChatController;
import code.sockets.WebSocketConfiguration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = WebSocketControllerSliceTest.MinimalSocketWebConfig.class
)
public class WebSocketControllerSliceTest {

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
    @Import({WebSocketConfiguration.class, ChatController.class})
    static class MinimalSocketWebConfig {}

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;

    @BeforeEach
    public void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @Test
    @SneakyThrows
    public void testControllerInIsolation() {

        String url = "ws://localhost:" + port + "/ws/websocket";
        StompSession session = stompClient
            .connectAsync(url, new StompSessionHandlerAdapter() {})
            .get(2, TimeUnit.SECONDS);

        CompletableFuture<ChatController.Greeting> resultKeeper = new CompletableFuture<>();

        session.subscribe("/topic/greetings", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) { return ChatController.Greeting.class; }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                resultKeeper.complete((ChatController.Greeting) payload);
            }
        });

        session.send("/app/hello", new ChatController.HelloMessage("Alice"));
        ChatController.Greeting response = resultKeeper.get(3, TimeUnit.SECONDS);
        System.out.println("Response:" + response);
    }
}