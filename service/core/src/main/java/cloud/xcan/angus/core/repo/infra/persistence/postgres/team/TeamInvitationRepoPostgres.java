package cloud.xcan.angus.core.repo.infra.persistence.postgres.team;

import cloud.xcan.angus.core.repo.domain.team.TeamInvitationRepo;
import org.springframework.stereotype.Repository;

@Repository("teamInvitationRepo")
public interface TeamInvitationRepoPostgres extends TeamInvitationRepo {
}
