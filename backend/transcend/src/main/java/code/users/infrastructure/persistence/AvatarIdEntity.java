package code.users.infrastructure.persistence;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
public record AvatarIdEntity(UUID val) implements Serializable {}