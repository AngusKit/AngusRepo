package cloud.xcan.angus.core.repo.domain.team;

import cloud.xcan.angus.core.jpa.repository.CustomBaseRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TeamMemberSearchRepo extends CustomBaseRepository<TeamMember> {
}
