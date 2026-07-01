package code.users.logic

import code.shared.bootstrap.RedisDaoTestSupport
import code.users.domain.model.SessionFixtures
import code.users.domain.model.SessionFixtures.DEVICE_INFO_FIXTURE
import code.users.domain.model.SessionFixtures.SESSION_ID_FIXTURE
import code.users.domain.model.UserFixtures.USER_ID_FIXTURE
import code.users.domain.model.UserFixtures.USER_UUID_FIXTURE
import code.users.infrastructure.cache.PresenceRepository
import code.users.ports.`in`.UpdatePresenceUseCase
import code.users.ports.out.PresenceDao
import io.kotest.matchers.shouldBe
import org.springframework.context.annotation.Import

@Import(PresenceRepository::class, UpdatePresence::class)
class PresenceTest(
  private val service: UpdatePresenceUseCase,
  private val presenceDao: PresenceDao
) : RedisDaoTestSupport() {

  init {
    Given("a user is currently offline") {
      presenceDao.isUserOnline(USER_ID_FIXTURE) shouldBe false
      When("the setUserOnline service is executed") {
        val command =
          UpdatePresenceUseCase.SetUserOnlineCommand(
            SESSION_ID_FIXTURE,
            USER_UUID_FIXTURE,
            DEVICE_INFO_FIXTURE
          )
        service.setUserOnline(command)
        val isOnline = presenceDao.isUserOnline(USER_ID_FIXTURE)

        Then("the presence DAO should report the user as online") { isOnline shouldBe true }
      }
    }

    Given("a user is currently online") {
      presenceDao.setSessionOnline(SessionFixtures.SESSION)
      presenceDao.isUserOnline(USER_ID_FIXTURE) shouldBe true

      When("the setUserOffline service is executed") {
        val command =
          UpdatePresenceUseCase.SetUserOfflineCommand(SESSION_ID_FIXTURE, USER_UUID_FIXTURE)
        service.setUserOffline(command)
        val isOnline = presenceDao.isUserOnline(USER_ID_FIXTURE)

        Then("the user should be offline in the database") { isOnline shouldBe false }
      }
    }
  }
}
