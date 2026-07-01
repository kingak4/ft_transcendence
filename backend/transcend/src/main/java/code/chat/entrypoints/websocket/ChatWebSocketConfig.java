package code.chat.entrypoints.websocket;

import static code.shared.config.WebSocketConfig.SOCKET_TOPIC;

import java.util.UUID;

public class ChatWebSocketConfig {
  public static final String CHAT_MESSAGES_TOPIC_PREFIX = SOCKET_TOPIC + "/chat/";
  public static final String CHAT_MESSAGES_TOPIC_SUFFIX = "/messages";
  public static final String CHAT_MESSAGES_TOPIC =
      CHAT_MESSAGES_TOPIC_PREFIX + "{chatId}" + CHAT_MESSAGES_TOPIC_SUFFIX;

  public static String chatMessagesTopic(UUID chatId) {
    return CHAT_MESSAGES_TOPIC.replace("{chatId}", chatId.toString());
  }
}
