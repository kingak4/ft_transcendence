package code.chat.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatJpaRepository extends JpaRepository<ChatEntity, ChatIdEntity> {
    @Query("select distinct c from ChatEntity c join c.participants p where p = :userId")
    Page<ChatEntity> findAllByParticipant(@Param("userId") UUID userId, Pageable pageable);

    @Query(
            "select c from ChatEntity c "
                    + "where size(c.participants) = 2 "
                    + "and :initiator member of c.participants "
                    + "and :recipient member of c.participants")
    Optional<ChatEntity> findByParticipants(
            @Param("initiator") UUID initiator, @Param("recipient") UUID recipient);
}
