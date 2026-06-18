package code.users.infrastructure.cache;

import code.users.domain.model.Session;
import code.users.domain.model.SessionId;
import code.users.domain.model.UserId;
import code.users.ports.out.PresenceDao;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PresenceRepository implements PresenceDao {

  private final StringRedisTemplate redisTemplate;
  private static final Duration SESSION_TTL = Duration.ofMinutes(10);
  private static final String SESSIONS_KEY_FMT = "presence:user:%s:sessions";
  private static final String SESSION_INFO_KEY_FMT = "presence:session:%s:info";

  @Override
  public void setSessionOnline(Session session) {
    String sessionsKey = sessionsKey(session.getUserId());
    String sessionInfoKey = sessionInfoKey(session.getId());

    Map<String, String> info = Map.of("userId", sessionsKey, "deviceInfo", session.getDeviceInfo());

    long now = OffsetDateTime.now().toInstant().toEpochMilli();
    double expirationTime = (double) now + SESSION_TTL.toMillis();

    redisTemplate.opsForZSet().add(sessionsKey, session.getId().val(), expirationTime);
    redisTemplate.expire(sessionsKey, SESSION_TTL);

    redisTemplate.opsForHash().putAll(sessionInfoKey, info);
    redisTemplate.expire(sessionInfoKey, SESSION_TTL);
  }

  @Override
  public boolean isUserOnline(UserId userId) {
    String sessionsKey = sessionsKey(userId);
    long now = OffsetDateTime.now().toInstant().toEpochMilli();

    Long activeSessionsCount =
        redisTemplate.opsForZSet().count(sessionsKey, now, Double.POSITIVE_INFINITY);

    return activeSessionsCount != null && activeSessionsCount > 0;
  }

  @Override
  public void removeSession(UserId userId, SessionId sessionId) {
    String sessionVal = sessionId.val();
    String sessionsKey = sessionsKey(userId);
    String sessionInfoKey = sessionInfoKey(sessionId);

    redisTemplate.opsForZSet().remove(sessionsKey, sessionVal);
    redisTemplate.delete(sessionInfoKey);
  }

  private String sessionsKey(UserId userId) {
    return String.format(SESSIONS_KEY_FMT, userId.val().toString());
  }

  public static String sessionInfoKey(SessionId sessionId) {
    return String.format(SESSION_INFO_KEY_FMT, sessionId.val());
  }
}
