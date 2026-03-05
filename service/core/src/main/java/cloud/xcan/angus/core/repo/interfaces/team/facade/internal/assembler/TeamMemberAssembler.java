package cloud.xcan.angus.core.repo.interfaces.team.facade.internal.assembler;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.repo.domain.team.TeamInvitation;
import cloud.xcan.angus.core.repo.domain.team.TeamMember;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.MemberInviteDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.TeamMemberFindDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamInvitationVo;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamMemberVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class TeamMemberAssembler {

  public static TeamInvitation toInvitationEntity(MemberInviteDto dto, Long invitedBy) {
    TeamInvitation invitation = new TeamInvitation();
    invitation.setEmail(dto.getEmail());
    invitation.setRole(dto.getRole());
    invitation.setMessage(dto.getMessage());
    invitation.setInvitedBy(invitedBy);
    invitation.setCreatedBy(invitedBy);
    return invitation;
  }

  public static TeamMemberVo toMemberVo(TeamMember member) {
    if (member == null) {
      return null;
    }
    TeamMemberVo vo = new TeamMemberVo();
    vo.setId(member.getId());
    vo.setUserId(member.getUserId());
    vo.setName(member.getName());
    vo.setEmail(member.getEmail());
    vo.setAvatar(member.getAvatar());
    vo.setRole(member.getRole());
    vo.setStatus(member.getStatus());
    vo.setJoinedDate(member.getJoinedDate());
    vo.setLastActive(member.getLastActive());
    return vo;
  }

  public static TeamInvitationVo toInvitationVo(TeamInvitation invitation) {
    if (invitation == null) {
      return null;
    }
    TeamInvitationVo vo = new TeamInvitationVo();
    vo.setId(invitation.getId());
    vo.setEmail(invitation.getEmail());
    vo.setRole(invitation.getRole());
    vo.setStatus(invitation.getStatus());
    vo.setMessage(invitation.getMessage());
    vo.setInvitedBy(invitation.getInvitedBy());
    vo.setInvitedDate(invitation.getInvitedDate());
    vo.setExpiresAt(invitation.getExpiresAt());
    vo.setAcceptedDate(invitation.getAcceptedDate());
    return vo;
  }

  public static GenericSpecification<TeamMember> getSpecification(TeamMemberFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "joinedDate")
        .matchSearchFields("name", "email")
        .orderByFields("id", "name", "createdDate", "joinedDate", "role", "status")
        .inAndNotFields("role", "status")
        .build();
    return new GenericSpecification<>(filters);
  }
}
