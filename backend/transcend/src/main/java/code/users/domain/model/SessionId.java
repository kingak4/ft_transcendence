package code.users.domain.model;

public record SessionId(String val) {
  public static SessionId of(String val) {
    return new SessionId(val);
  }
}
