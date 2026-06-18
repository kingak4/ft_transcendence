package code.users.domain.model;

import java.time.OffsetDateTime;

public class SessionFixtures {
  public static final String SESSION_ID_FIXTURE = "sess-123";
  public static final String DEVICE_INFO_FIXTURE = "web-client";
  public static final Session SESSION =
      Session.builder()
          .id(SessionId.of(SESSION_ID_FIXTURE))
          .userId(UserFixtures.USER_ID_FIXTURE)
          .deviceInfo(DEVICE_INFO_FIXTURE)
          .createdAt(OffsetDateTime.now())
          .build();
}
