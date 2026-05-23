package code.users.infrastructure.cache;

import code.users.domain.model.Session;
import code.users.domain.model.SessionId;
import code.users.domain.model.UserFixtures;
import code.users.domain.model.UserId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;

import static code.users.domain.model.UserFixtures.SESSION_FIXTURE;
import static code.users.infrastructure.cache.PresenceRepository.sessionInfoKey;
import static code.users.infrastructure.cache.PresenceRepository.sessionsKey;
import static org.assertj.core.api.Assertions.assertThat;

@DataRedisTest
class PresenceRepositoryTest {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private PresenceRepository dao;

  @Test
  void setSessionOnline_StoresAndSetsTTL() {
    // given
    UserId userId = UserFixtures.USER_ID_FIXTURE;
    String sessionId = SESSION_FIXTURE;
    Session session = Session.builder().id(SessionId.of(sessionId)).userId(userId).deviceInfo("mobile").build();

    // when
    dao.setSessionOnline(session);

    // then
    Double score = redisTemplate.opsForZSet().score(sessionsKey(String.valueOf(userId)), sessionId);
    assertThat(score).isGreaterThan(System.currentTimeMillis());

    Map<Object, Object> info = redisTemplate.opsForHash().entries(sessionInfoKey(sessionId));
    assertThat(info).containsEntry("userId", userId.toString()).containsEntry("deviceInfo", "mobile");

    assertThat(redisTemplate.getExpire(sessionsKey(String.valueOf(userId)))).isPositive();
  }

  @Test
  void removeSession_CleansUpAllKeys() {
    // given
    UserId userId = UserFixtures.USER_ID_FIXTURE;
    String sessionId = SESSION_FIXTURE;
    String zsetKey = sessionsKey(String.valueOf(userId));
    String hashKey = sessionInfoKey(sessionId);

    redisTemplate.opsForZSet().add(zsetKey, sessionId, System.currentTimeMillis() + 10000);
    redisTemplate.opsForHash().put(hashKey, "dummy", "data");

    // when
    dao.removeSession(userId, SessionId.of(sessionId));

    // then
    assertThat(redisTemplate.opsForZSet().score(zsetKey, sessionId)).isNull();
    assertThat(redisTemplate.hasKey(hashKey)).isFalse();
  }

  @Test
  void isUserOnline_ReflectsPresenceBasedOnScores() {
    // given
    UserId userId = UserFixtures.USER_ID_FIXTURE;
    String key = sessionsKey(String.valueOf(userId));

    // then
    assertThat(dao.isUserOnline(userId)).isFalse();

    // when
    redisTemplate.opsForZSet().add(key, "s1", System.currentTimeMillis() + 5000);

    // then
    assertThat(dao.isUserOnline(userId)).isTrue();

    // when
    redisTemplate.opsForZSet().add(key, "s2", System.currentTimeMillis() - 5000);

    // then
    assertThat(dao.isUserOnline(userId)).isTrue();

    // when
    redisTemplate.opsForZSet().remove(key, "s1");

    // then
    assertThat(dao.isUserOnline(userId)).isFalse();
  }
}