package code.users.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserDetailsJpaRepository extends JpaRepository<UserDetailsEntity, UserIdEntity> {
  @Query(
      value =
          """
            SELECT uf.friend_id, ud.display_name, ud.avatar_id
            FROM user_friends uf
            JOIN users u ON u.val = uf.friend_id
            JOIN user_details ud ON ud.val = u.user_details_id
            WHERE uf.user_id = :userId
            """,
      nativeQuery = true)
  List<Object[]> findFriendDetailsByUserId(@Param("userId") UUID userId, Pageable pageable);
}
