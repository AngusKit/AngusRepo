package cloud.xcan.angus.core.repo.infra.persistence.postgres.team;

import cloud.xcan.angus.core.repo.domain.team.TeamMemberRepo;
import org.springframework.stereotype.Repository;

@Repository("teamMemberRepo")
public interface TeamMemberRepoPostgres extends TeamMemberRepo {
}
