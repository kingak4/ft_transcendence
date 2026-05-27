package code.chat.domain.model;

import java.util.UUID;

public record UserId(UUID val) {
    public static UserId of(UUID val) {
        return new UserId(val);
    }
}
