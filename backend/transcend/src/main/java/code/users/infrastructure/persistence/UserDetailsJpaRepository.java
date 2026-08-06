package code.users.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDetailsJpaRepository extends JpaRepository<UserDetailsEntity, UserIdEntity> {

  Page<UserDetailsEntity> findByDisplayNameContainingIgnoreCase(
      String displayName, Pageable pageable);

  @Query(
      value =
          """
              SELECT uf.friend_id, ud.display_name, ud.avatar_id
              FROM user_friends uf
              JOIN users u ON u.val = uf.friend_id
              JOIN user_details ud ON ud.val = u.val
              WHERE uf.user_id = :userId
              ORDER BY ud.display_name, uf.friend_id
            """,
      countQuery =
          """
              SELECT count(*)
              FROM user_friends uf
              WHERE uf.user_id = :userId
            """,
      nativeQuery = true)
  Page<Object[]> findFriendDetailsByUserId(@Param("userId") UUID userId, Pageable pageable);
}
