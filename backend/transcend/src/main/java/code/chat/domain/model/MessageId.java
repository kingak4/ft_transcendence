package code.chat.domain.model;

import java.util.UUID;

public record MessageId(UUID val) {
    public static MessageId of(UUID val) {
        return new MessageId(val);
    }
    public static MessageId generate() {
        return new MessageId(UUID.randomUUID());
    }
}
