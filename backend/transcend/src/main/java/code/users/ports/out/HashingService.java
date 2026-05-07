<<<<<<< HEAD:backend/transcend/src/main/java/code/modules/users/ports/out/HashingService.java
package code.modules.users.ports.out;

public interface HashingService {

  boolean matches(String rawPassword, String encodedPassword);

  String encode(String rawPassword);
}
=======
package code.users.ports.out;

public interface HashingService {

  boolean matches(String rawPassword, String encodedPassword);

  String encode(String rawPassword);
}
>>>>>>> main:backend/transcend/src/main/java/code/users/ports/out/HashingService.java
