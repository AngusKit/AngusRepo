package cloud.xcan.angus.core.repo.application.cmd.team.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.team.TeamMemberCmd;
import cloud.xcan.angus.core.repo.domain.team.InvitationStatus;
import cloud.xcan.angus.core.repo.domain.team.MemberStatus;
import cloud.xcan.angus.core.repo.domain.team.TeamInvitation;
import cloud.xcan.angus.core.repo.domain.team.TeamInvitationRepo;
import cloud.xcan.angus.core.repo.domain.team.TeamMember;
import cloud.xcan.angus.core.repo.domain.team.TeamMemberRepo;
import cloud.xcan.angus.core.repo.domain.team.UserRole;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class TeamMemberCmdImpl extends CommCmd<TeamMember, Long> implements TeamMemberCmd {

  @Resource
  private TeamMemberRepo teamMemberRepo;

  @Resource
  private TeamInvitationRepo teamInvitationRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public TeamInvitation invite(TeamInvitation invitation) {
    return new BizTemplate<TeamInvitation>() {
      @Override
      protected void checkParams() {
        if (teamInvitationRepo.existsByEmailAndStatus(invitation.getEmail(), InvitationStatus.PENDING)) {
          throw new RuntimeException("该邮箱已有待处理的邀请");
        }
      }

      @Override
      protected TeamInvitation process() {
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setInvitedDate(LocalDateTime.now());
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));
        invitation.setCreatedDate(LocalDateTime.now());
        teamInvitationRepo.save(invitation);
        return invitation;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public TeamMember updateRole(Long memberId, UserRole role) {
    return new BizTemplate<TeamMember>() {
      TeamMember existing;

      @Override
      protected void checkParams() {
        existing = teamMemberRepo.findById(memberId)
            .orElseThrow(() -> new RuntimeException("成员不存在: " + memberId));
      }

      @Override
      protected TeamMember process() {
        existing.setRole(role);
        teamMemberRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void removeMember(Long memberId) {
    teamMemberRepo.deleteById(memberId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public TeamInvitation resendInvitation(Long invitationId) {
    return new BizTemplate<TeamInvitation>() {
      TeamInvitation existing;

      @Override
      protected void checkParams() {
        existing = teamInvitationRepo.findById(invitationId)
            .orElseThrow(() -> new RuntimeException("邀请不存在: " + invitationId));
      }

      @Override
      protected TeamInvitation process() {
        existing.setToken(UUID.randomUUID().toString());
        existing.setExpiresAt(LocalDateTime.now().plusDays(7));
        existing.setStatus(InvitationStatus.PENDING);
        teamInvitationRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void revokeInvitation(Long invitationId) {
    teamInvitationRepo.deleteById(invitationId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public TeamMember acceptInvitation(String token, String name, String password) {
    return new BizTemplate<TeamMember>() {
      TeamInvitation invitation;

      @Override
      protected void checkParams() {
        invitation = teamInvitationRepo.findByToken(token)
            .orElseThrow(() -> new RuntimeException("邀请不存在或已失效"));
        if (invitation.getStatus() != InvitationStatus.PENDING) {
          throw new RuntimeException("邀请已被处理");
        }
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
          throw new RuntimeException("邀请已过期");
        }
      }

      @Override
      protected TeamMember process() {
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setAcceptedDate(LocalDateTime.now());
        teamInvitationRepo.save(invitation);

        TeamMember member = new TeamMember();
        member.setName(name);
        member.setEmail(invitation.getEmail());
        member.setRole(invitation.getRole());
        member.setStatus(MemberStatus.ACTIVE);
        member.setJoinedDate(LocalDateTime.now());
        member.setCreatedDate(LocalDateTime.now());
        insert0(member);
        return member;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<TeamMember, Long> getRepository() {
    return this.teamMemberRepo;
  }
}
