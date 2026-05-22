package code.users.ports.out;

import code.users.domain.model.Session;
import code.users.domain.model.SessionId;
import java.util.Optional;

public interface SessionDao {

  void saveSession(Session session);

  void deleteSession(SessionId sessionId);

  Optional<Session> findById(SessionId sessionId);
}
