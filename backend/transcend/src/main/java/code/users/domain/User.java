package code.users.domain;

import java.util.UUID;

public record User(UUID id, String email, String password) {}
