package code.users.ports.out;

public interface HashingService {

  boolean matches(String rawPassword, String encodedPassword);

  String encode(String rawPassword);
}
