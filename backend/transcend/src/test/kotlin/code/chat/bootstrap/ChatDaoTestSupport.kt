package code.chat.bootstrap

import code.bootstrap.DotEnvInitializer
import code.chat.domain.model.Chat
import code.chat.domain.model.ChatFixtures
import code.chat.domain.model.ChatId
import code.chat.domain.model.ChatUserFixtures.*
import code.chat.domain.model.UserId
import code.chat.infrastructure.persistence.ChatRepository
import code.chat.logic.MembershipValidator
import code.chat.ports.out.ChatDao
import code.users.bootstrap.DefaultAvatarInitializer
import code.users.domain.model.Role
import code.users.infrastructure.persistence.UserEntityMapperImpl
import code.users.infrastructure.persistence.UserRepository
import code.users.ports.out.UserDao
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor

@DataJpaTest
@ActiveProfiles("test")
@EnableMethodSecurity
@ContextConfiguration(initializers = [DotEnvInitializer::class])
@Import(
  UserRepository::class,
  ChatRepository::class,
  UserEntityMapperImpl::class,
  DefaultAvatarInitializer::class,
  MembershipValidator::class,
  MethodValidationPostProcessor::class
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class ChatDaoTestSupport : BehaviorSpec() {

  @Autowired lateinit var chatDao: ChatDao

  @Autowired lateinit var userDao: UserDao

  protected lateinit var chatId: ChatId

  protected fun authenticateAs(userId: UserId, role: Role) {
    val authority = SimpleGrantedAuthority(role.name)
    val auth =
      UsernamePasswordAuthenticationToken(userId.`val`().toString(), null, listOf(authority))
    SecurityContextHolder.getContext().authentication = auth
  }

  protected fun clearAuthentication() {
    SecurityContextHolder.clearContext()
  }

  private fun createChatWithMessages(participants: Set<UserId>, messageCount: Int): ChatId {
    val chat = Chat.builder().participants(participants).build()
    val chatId = chatDao.createChat(chat)
    repeat(messageCount) { _ ->
      val randomSenderId = participants.random()
      val message = ChatFixtures.aMessageBuilder(randomSenderId).build()
      chatDao.saveMessage(message)
    }
    return chatId
  }

  init {
    extension(SpringExtension)
    beforeSpec {
      userDao.createUser(aChatDaoUser())
      userDao.createUser(aChatMember1DaoUser())
      userDao.createUser(aChatMember2DaoUser())
      val participants = setOf(CHAT_USER_ID_FIXTURE, CHAT_MEMBER1_ID_FIXTURE)
      val chatId = createChatWithMessages(participants, 5)
    }
    afterSpec { clearAuthentication() }
  }
}
