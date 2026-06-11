package code.users.domain.model;

import lombok.With;

@With
public record Avatar(AvatarId id, byte[] content) {}
