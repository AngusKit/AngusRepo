package cloud.xcan.angus.core.repo.domain.team;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Optional;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TeamMemberRepo extends BaseRepository<TeamMember, Long> {

  Optional<TeamMember> findByUserId(Long userId);

  Optional<TeamMember> findByEmail(String email);

  boolean existsByUserId(Long userId);

  long countByRole(UserRole role);

  long countByStatus(MemberStatus status);
}
