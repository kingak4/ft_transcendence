package code.users.domain.model;

import java.util.UUID;

public record User(UUID id, String email, String password) {}
