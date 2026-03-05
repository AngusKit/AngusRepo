package cloud.xcan.angus.core.repo.application.cmd.team;

import cloud.xcan.angus.core.repo.domain.team.TeamInvitation;
import cloud.xcan.angus.core.repo.domain.team.TeamMember;
import cloud.xcan.angus.core.repo.domain.team.UserRole;

public interface TeamMemberCmd {

  TeamInvitation invite(TeamInvitation invitation);

  TeamMember updateRole(Long memberId, UserRole role);

  void removeMember(Long memberId);

  TeamInvitation resendInvitation(Long invitationId);

  void revokeInvitation(Long invitationId);

  TeamMember acceptInvitation(String token, String name, String password);
}
