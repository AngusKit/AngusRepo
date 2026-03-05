package cloud.xcan.angus.core.repo.interfaces.team.facade;

import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.InvitationAcceptDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.MemberInviteDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.MemberRoleUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.TeamMemberFindDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.InvitationAcceptResultVo;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamInvitationVo;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamMemberVo;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamStatisticsVo;
import cloud.xcan.angus.remote.PageResult;

public interface TeamMemberFacade {

  TeamInvitationVo invite(MemberInviteDto dto);

  TeamMemberVo updateRole(Long memberId, MemberRoleUpdateDto dto);

  void removeMember(Long memberId);

  TeamMemberVo getMemberById(Long id);

  PageResult<TeamMemberVo> listMembers(TeamMemberFindDto dto);

  TeamStatisticsVo getStatistics();

  PageResult<TeamInvitationVo> listInvitations(TeamMemberFindDto dto);

  TeamInvitationVo resendInvitation(Long invitationId);

  void revokeInvitation(Long invitationId);

  InvitationAcceptResultVo acceptInvitation(String token, InvitationAcceptDto dto);
}
