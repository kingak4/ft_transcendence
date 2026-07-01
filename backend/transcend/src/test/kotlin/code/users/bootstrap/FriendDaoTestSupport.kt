package code.users.bootstrap

import code.users.domain.model.FriendFixtures

class FriendDaoTestSupport : UserDaoTestSupport() {

  init {
    beforeSpec {
      userDao.createUser(FriendFixtures.aFriend1DaoUser())
      userDao.createUser(FriendFixtures.aFriend2DaoUser())
    }
  }
}
