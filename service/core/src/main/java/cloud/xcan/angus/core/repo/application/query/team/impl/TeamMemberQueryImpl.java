package cloud.xcan.angus.core.repo.application.query.team.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.team.TeamMemberQuery;
import cloud.xcan.angus.core.repo.domain.team.InvitationStatus;
import cloud.xcan.angus.core.repo.domain.team.MemberStatus;
import cloud.xcan.angus.core.repo.domain.team.TeamInvitation;
import cloud.xcan.angus.core.repo.domain.team.TeamInvitationRepo;
import cloud.xcan.angus.core.repo.domain.team.TeamMember;
import cloud.xcan.angus.core.repo.domain.team.TeamMemberListRepo;
import cloud.xcan.angus.core.repo.domain.team.TeamMemberRepo;
import cloud.xcan.angus.core.repo.domain.team.TeamMemberSearchRepo;
import cloud.xcan.angus.core.repo.domain.team.UserRole;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamStatisticsVo;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class TeamMemberQueryImpl implements TeamMemberQuery {

  @Resource
  private TeamMemberRepo teamMemberRepo;

  @Resource
  private TeamMemberListRepo teamMemberListRepo;

  @Resource
  private TeamMemberSearchRepo teamMemberSearchRepo;

  @Resource
  private TeamInvitationRepo teamInvitationRepo;

  @Override
  public Page<TeamMember> find(GenericSpecification<TeamMember> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<TeamMember>>() {
      @Override
      protected Page<TeamMember> process() {
        return fullTextSearch
            ? teamMemberSearchRepo.find(spec.getCriteria(), pageable, TeamMember.class, match)
            : teamMemberListRepo.find(spec.getCriteria(), pageable, TeamMember.class, null);
      }
    }.execute();
  }

  @Override
  public Optional<TeamMember> findById(Long id) {
    return teamMemberRepo.findById(id);
  }

  @Override
  public TeamMember findAndCheck(Long id) {
    return teamMemberRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("成员不存在: " + id));
  }

  @Override
  public TeamStatisticsVo getStatistics() {
    return new BizTemplate<TeamStatisticsVo>() {
      @Override
      protected TeamStatisticsVo process() {
        TeamStatisticsVo stats = new TeamStatisticsVo();
        stats.setTotalMembers(teamMemberRepo.count());
        stats.setActiveMembers(teamMemberRepo.countByStatus(MemberStatus.ACTIVE));
        stats.setAdminCount(teamMemberRepo.countByRole(UserRole.ADMIN));
        stats.setDeveloperCount(teamMemberRepo.countByRole(UserRole.DEVELOPER));
        stats.setViewerCount(teamMemberRepo.countByRole(UserRole.VIEWER));
        stats.setPendingInvitations((long) teamInvitationRepo.findByStatus(InvitationStatus.PENDING).size());
        return stats;
      }
    }.execute();
  }

  @Override
  public Page<TeamInvitation> findInvitations(PageRequest pageable) {
    return teamInvitationRepo.findAll(pageable);
  }
}
