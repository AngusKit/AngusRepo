package cloud.xcan.angus.core.repo.domain.team;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TeamInvitationRepo extends BaseRepository<TeamInvitation, Long> {

  Optional<TeamInvitation> findByToken(String token);

  List<TeamInvitation> findByEmail(String email);

  List<TeamInvitation> findByStatus(InvitationStatus status);

  boolean existsByEmailAndStatus(String email, InvitationStatus status);
}
