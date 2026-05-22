package code.users.ports.out;

import code.users.domain.model.Session;
import code.users.domain.model.SessionId;
import code.users.domain.model.UserId;

public interface PresenceDao {

  void removeSession(UserId userId, SessionId sessionId);

  void setSessionOnline(Session session);

  boolean isUserOnline(UserId userId);
}
