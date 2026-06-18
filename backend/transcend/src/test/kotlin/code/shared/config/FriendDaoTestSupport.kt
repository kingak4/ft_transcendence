package code.shared.config

import code.users.domain.model.FriendFixtures

class FriendDaoTestSupport : DaoTestSupport() {

  init {
    beforeSpec {
      userDao.createUser(FriendFixtures.aFriend1DaoUser())
      userDao.createUser(FriendFixtures.aFriend2DaoUser())
    }
  }
}