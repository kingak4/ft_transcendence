package code.users.ports.in;

public interface UpdateUserStatusUseCase {
  void setUserOnline(String username, String sessionId);

  void setUserOffline(String sessionId);
}
