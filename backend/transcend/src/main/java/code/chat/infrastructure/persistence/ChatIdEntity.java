package code.chat.infrastructure.persistence;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record ChatIdEntity(UUID val) implements Serializable {}
