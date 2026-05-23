package code.users.infrastructure.cache;

import code.users.domain.model.Session;
import code.users.domain.model.SessionId;
import code.users.domain.model.UserId;
import code.users.ports.out.PresenceDao;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PresenceRepository implements PresenceDao {

  private final StringRedisTemplate redisTemplate;
  private static final Duration SESSION_TTL = Duration.ofSeconds(60);
  private static final String SESSIONS_KEY_FMT = "presence:user:%s:sessions";
  private static final String SESSION_INFO_KEY_FMT = "presence:session:%s:info";

  @Override
  public void setSessionOnline(Session session) {
    String sessionId = session.getId().val();
    String userIdStr = session.getUserId().toString();
    String sessionsKey = sessionsKey(userIdStr);
    String sessionInfoKey = sessionInfoKey(sessionId);

    Map<String, String> info = Map.of("userId", userIdStr, "deviceInfo", session.getDeviceInfo());

    long expirationTime = System.currentTimeMillis() + SESSION_TTL.toMillis();

    redisTemplate.executePipelined(
        new SessionCallback<Object>() {
          @Override
          public <K, V> Object execute(@NonNull RedisOperations<K, V> operations)
              throws DataAccessException {
            StringRedisTemplate ops = (StringRedisTemplate) operations;

            ops.opsForZSet().add(sessionsKey, sessionId, expirationTime);
            ops.expire(sessionsKey, SESSION_TTL);
            ops.opsForHash().putAll(sessionInfoKey, info);
            ops.expire(sessionInfoKey, SESSION_TTL);

            return null;
          }
        });
  }

  @Override
  public void removeSession(UserId userId, SessionId sessionId) {
    String sessionStr = sessionId.val();
    String sessionsKey = sessionsKey(userId.toString());
    String sessionInfoKey = sessionInfoKey(sessionStr);

    redisTemplate.executePipelined(
        new SessionCallback<Object>() {
          @Override
          public <K, V> Object execute(RedisOperations<K, V> operations)
              throws DataAccessException {
            StringRedisTemplate ops = (StringRedisTemplate) operations;

            ops.opsForZSet().remove(sessionsKey, sessionStr);
            ops.delete(sessionInfoKey);

            return null;
          }
        });
  }

  @Override
  public boolean isUserOnline(UserId userId) {
    String sessionsKey = sessionsKey(userId.toString());
    long now = System.currentTimeMillis();

    Long activeSessionsCount =
        redisTemplate.opsForZSet().count(sessionsKey, now, Double.POSITIVE_INFINITY);

    return activeSessionsCount != null && activeSessionsCount > 0;
  }

  public static String sessionsKey(String userId) {
    return String.format(SESSIONS_KEY_FMT, userId);
  }

  public static String sessionInfoKey(String sessionId) {
    return String.format(SESSION_INFO_KEY_FMT, sessionId);
  }
}