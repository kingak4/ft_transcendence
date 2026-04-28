package code.users.infrastructure.persistence;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record UserIdEntity(UUID value) implements Serializable {}
