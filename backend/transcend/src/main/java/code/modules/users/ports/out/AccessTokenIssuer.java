package code.modules.users.ports.out;

public interface AccessTokenIssuer {

  String generateToken(String subject);
}
