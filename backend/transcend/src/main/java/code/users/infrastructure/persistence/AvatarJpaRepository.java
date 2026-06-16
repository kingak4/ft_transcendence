package code.users.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AvatarJpaRepository extends JpaRepository<AvatarEntity, AvatarIdEntity> {}