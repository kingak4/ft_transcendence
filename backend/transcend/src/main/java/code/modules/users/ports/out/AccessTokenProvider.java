package code.modules.users.ports.out;

public interface AccessTokenProvider {

  String generateToken(String subject);
}
