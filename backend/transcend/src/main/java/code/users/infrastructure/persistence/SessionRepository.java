package code.users.infrastructure.persistence;

import code.shared.exceptions.NotImplementedException;
import code.users.domain.model.Session;
import code.users.domain.model.SessionId;
import code.users.ports.out.SessionDao;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SessionRepository implements SessionDao {

  //  private final SessionJpaRepository sessionJpaRepository;
  //  private final SessionEntityMapper sessionEntityMapper;

  @Override
  public void saveSession(Session session) {
    throw new NotImplementedException();
  }

  @Override
  public void deleteSession(SessionId sessionId) {
    throw new NotImplementedException();
  }

  @Override
  public Optional<Session> findById(SessionId sessionId) {
    throw new NotImplementedException();
  }
}
