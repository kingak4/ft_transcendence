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

    @Query(
            value = """
                SELECT DISTINCT c.*
                FROM chat c
                JOIN chat_participants cp ON cp.chat_id = c.val
                WHERE cp.user_id = :userId
                """,
            countQuery = """
                SELECT count(DISTINCT c.val)
                FROM chat c
                JOIN chat_participants cp ON cp.chat_id = c.val
                WHERE cp.user_id = :userId
                """,
            nativeQuery = true)
    Page<ChatEntity> findAllByParticipant(@Param("userId") UUID userId, Pageable pageable);

    @Query(
            value = """
                SELECT c.*
                FROM chat c
                WHERE (SELECT count(*) FROM chat_participants cp WHERE cp.chat_id = c.val) = 2
                  AND EXISTS (
                      SELECT 1 FROM chat_participants cp
                      WHERE cp.chat_id = c.val AND cp.user_id = :initiator
                  )
                  AND EXISTS (
                      SELECT 1 FROM chat_participants cp
                      WHERE cp.chat_id = c.val AND cp.user_id = :recipient
                  )
                """,
            nativeQuery = true)
    Optional<ChatEntity> findByParticipants(
            @Param("initiator") UUID initiator, @Param("recipient") UUID recipient);

    @Query(
            value = """
                SELECT cp1.chat_id, cp2.user_id, ud.display_name, ud.avatar_id
                FROM chat_participants cp1
                JOIN chat_participants cp2
                     ON cp2.chat_id = cp1.chat_id AND cp2.user_id <> :userId
                JOIN user_details ud ON ud.val = cp2.user_id
                WHERE cp1.user_id = :userId
                """,
            countQuery = """
                SELECT count(*)
                FROM chat_participants cp1
                WHERE cp1.user_id = :userId
                """,
            nativeQuery = true)
    Page<Object[]> findChatSummariesByUserId(@Param("userId") UUID userId, Pageable pageable);
}