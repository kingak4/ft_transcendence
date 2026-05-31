package code.chat.entrypoints.http;

import static code.chat.domain.model.ChatFixtures.CHAT_ID_FIXTURE;
import static code.chat.domain.model.ChatFixtures.MESSAGE_ID_FIXTURE;
import static code.chat.domain.model.ChatFixtures.USER_ID_1_FIXTURE;
import static code.chat.domain.model.ChatFixtures.USER_ID_2_FIXTURE;
import static code.chat.domain.model.ChatFixtures.aDefaultMessage;
import static code.chat.entrypoints.http.ChatController.BASE_URL;
import static code.chat.entrypoints.http.ChatController.CHAT_MESSAGES_ENDPOINT;
import static code.chat.entrypoints.http.ChatController.START_CHAT_ENDPOINT;
import static code.shared.entrypoints.UrlBuilderUtil.buildUrl;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import code.chat.domain.model.ChatId;
import code.chat.domain.model.Message;
import code.chat.domain.model.UserId;
import code.chat.ports.in.GetChatMessagesUseCase;
import code.chat.ports.in.GetChatsUseCase;
import code.chat.ports.in.StartChatUseCase;
import code.users.infrastructure.security.JwtAuthenticationFilter;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ChatController.class)
@AutoConfigureMockMvc(addFilters = false)
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
class ChatControllerTest {

  private static final UUID AUTH_USER_ID = UUID.randomUUID();

  private final MockMvc mockMvc;

  @MockitoBean private StartChatUseCase startChatUseCase;
  @MockitoBean private GetChatsUseCase getChatsUseCase;
  @MockitoBean private GetChatMessagesUseCase getChatMessagesUseCase;
  @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Test
  void startChatSuccessfully() throws Exception {
    UUID chatId = UUID.randomUUID();
    when(startChatUseCase.startChat(any(StartChatUseCase.StartChatCommand.class)))
        .thenReturn(ChatId.of(chatId));

    mockMvc
        .perform(
            post(buildUrl(BASE_URL, START_CHAT_ENDPOINT, USER_ID_2_FIXTURE))
                .principal(authentication()))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.chatId").value(chatId.toString()));

    verify(startChatUseCase)
        .startChat(
            new StartChatUseCase.StartChatCommand(
                UserId.of(AUTH_USER_ID), UserId.of(USER_ID_2_FIXTURE)));
  }

  @Test
  void getChatsSuccessfully() throws Exception {
    List<ChatId> chatIdFixture = List.of(ChatId.of(CHAT_ID_FIXTURE));
    when(getChatsUseCase.getChatList(UserId.of(AUTH_USER_ID), 0, 10)).thenReturn(chatIdFixture);

    mockMvc
        .perform(
            get(buildUrl(BASE_URL, null))
                .param("page", "0")
                .param("size", "10")
                .principal(authentication()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].chatId").value(CHAT_ID_FIXTURE.toString()));

    verify(getChatsUseCase).getChatList(UserId.of(AUTH_USER_ID), 0, 10);
  }

  @Test
  void getChatMessagesSuccessfully() throws Exception {
    Message message = aDefaultMessage();
    when(getChatMessagesUseCase.getChatMessages(ChatId.of(CHAT_ID_FIXTURE), 0, 10))
        .thenReturn(List.of(message));

    mockMvc
        .perform(
            get(buildUrl(BASE_URL, CHAT_MESSAGES_ENDPOINT, CHAT_ID_FIXTURE))
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].messageId").value(MESSAGE_ID_FIXTURE.toString()))
        .andExpect(jsonPath("$[0].senderId").value(USER_ID_1_FIXTURE.toString()));

    verify(getChatMessagesUseCase).getChatMessages(ChatId.of(CHAT_ID_FIXTURE), 0, 10);
  }

  private UsernamePasswordAuthenticationToken authentication() {
    return new UsernamePasswordAuthenticationToken(AUTH_USER_ID.toString(), null);
  }
}