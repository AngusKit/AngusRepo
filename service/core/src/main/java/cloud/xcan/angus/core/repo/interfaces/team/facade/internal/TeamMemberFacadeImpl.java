package cloud.xcan.angus.core.repo.interfaces.team.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.repo.interfaces.team.facade.internal.assembler.TeamMemberAssembler.getSpecification;
import static cloud.xcan.angus.core.repo.interfaces.team.facade.internal.assembler.TeamMemberAssembler.toInvitationEntity;
import static cloud.xcan.angus.core.repo.interfaces.team.facade.internal.assembler.TeamMemberAssembler.toInvitationVo;
import static cloud.xcan.angus.core.repo.interfaces.team.facade.internal.assembler.TeamMemberAssembler.toMemberVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.repo.application.cmd.team.TeamMemberCmd;
import cloud.xcan.angus.core.repo.application.query.team.TeamMemberQuery;
import cloud.xcan.angus.core.repo.domain.team.TeamInvitation;
import cloud.xcan.angus.core.repo.domain.team.TeamMember;
import cloud.xcan.angus.core.repo.interfaces.team.facade.TeamMemberFacade;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.InvitationAcceptDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.MemberInviteDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.MemberRoleUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.TeamMemberFindDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.internal.assembler.TeamMemberAssembler;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.InvitationAcceptResultVo;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamInvitationVo;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamMemberVo;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamStatisticsVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TeamMemberFacadeImpl implements TeamMemberFacade {

  @Resource
  private TeamMemberCmd teamMemberCmd;

  @Resource
  private TeamMemberQuery teamMemberQuery;

  @Override
  public TeamInvitationVo invite(MemberInviteDto dto) {
    // TODO: Replace with PrincipalContext.getUserId() when security context is available
    Long invitedBy = 1L;
    TeamInvitation invitation = toInvitationEntity(dto, invitedBy);
    TeamInvitation created = teamMemberCmd.invite(invitation);
    return toInvitationVo(created);
  }

  @Override
  public TeamMemberVo updateRole(Long memberId, MemberRoleUpdateDto dto) {
    TeamMember updated = teamMemberCmd.updateRole(memberId, dto.getRole());
    return toMemberVo(updated);
  }

  @Override
  public void removeMember(Long memberId) {
    teamMemberCmd.removeMember(memberId);
  }

  @Override
  public TeamMemberVo getMemberById(Long id) {
    TeamMember member = teamMemberQuery.findAndCheck(id);
    return toMemberVo(member);
  }

  @Override
  public PageResult<TeamMemberVo> listMembers(TeamMemberFindDto dto) {
    Page<TeamMember> page = teamMemberQuery.find(
        getSpecification(dto),
        dto.tranPage(),
        dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, TeamMemberAssembler::toMemberVo);
  }

  @Override
  public TeamStatisticsVo getStatistics() {
    return teamMemberQuery.getStatistics();
  }

  @Override
  public PageResult<TeamInvitationVo> listInvitations(TeamMemberFindDto dto) {
    Page<TeamInvitation> page = teamMemberQuery.findInvitations(dto.tranPage());
    return buildVoPageResult(page, TeamMemberAssembler::toInvitationVo);
  }

  @Override
  public TeamInvitationVo resendInvitation(Long invitationId) {
    TeamInvitation resent = teamMemberCmd.resendInvitation(invitationId);
    return toInvitationVo(resent);
  }

  @Override
  public void revokeInvitation(Long invitationId) {
    teamMemberCmd.revokeInvitation(invitationId);
  }

  @Override
  public InvitationAcceptResultVo acceptInvitation(String token, InvitationAcceptDto dto) {
    InvitationAcceptResultVo result = new InvitationAcceptResultVo();
    try {
      TeamMember member = teamMemberCmd.acceptInvitation(token, dto.getName(), dto.getPassword());
      result.setSuccess(true);
      result.setMessage("邀请接受成功");
      result.setMember(toMemberVo(member));
    } catch (Exception e) {
      result.setSuccess(false);
      result.setMessage("接受邀请失败");
    }
    return result;
  }
}
