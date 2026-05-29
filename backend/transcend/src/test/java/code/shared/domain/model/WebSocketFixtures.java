package code.shared.domain.model;

import java.util.UUID;

public final class WebSocketFixtures {
  public static final UUID ID_FIXTURE = UUID.randomUUID();
  public static final String SESSION_FIXTURE = UUID.randomUUID().toString();
  public static final String TOKEN_FIXTURE = "token-fixture";
  public static final String PASSWORD_FIXTURE = "password-fixture";

  private WebSocketFixtures() {}
}
