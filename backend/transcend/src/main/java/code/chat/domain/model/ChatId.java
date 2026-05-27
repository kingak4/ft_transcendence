package code.chat.domain.model;

import java.util.UUID;

public record ChatId(UUID val) {
    public static ChatId of(UUID val) {
        return new ChatId(val);
    }
    public static ChatId generate() {
        return new ChatId(UUID.randomUUID());
    }
}