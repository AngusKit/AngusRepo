package cloud.xcan.angus.core.repo.application.query.team;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.domain.team.TeamInvitation;
import cloud.xcan.angus.core.repo.domain.team.TeamMember;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamStatisticsVo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface TeamMemberQuery {

  Page<TeamMember> find(GenericSpecification<TeamMember> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  Optional<TeamMember> findById(Long id);

  TeamMember findAndCheck(Long id);

  TeamStatisticsVo getStatistics();

  Page<TeamInvitation> findInvitations(PageRequest pageable);
}
