package code.users.ports.out;

public interface AccessTokenProvider {

  String generateToken(String subject);
}
