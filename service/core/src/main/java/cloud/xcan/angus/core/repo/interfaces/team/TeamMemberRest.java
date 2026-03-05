package cloud.xcan.angus.core.repo.interfaces.team;

import cloud.xcan.angus.core.repo.interfaces.team.facade.TeamMemberFacade;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.InvitationAcceptDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.MemberInviteDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.MemberRoleUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.dto.TeamMemberFindDto;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.InvitationAcceptResultVo;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamInvitationVo;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamMemberVo;
import cloud.xcan.angus.core.repo.interfaces.team.facade.vo.TeamStatisticsVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TeamManagement", description = "团队管理 - 成员邀请、角色管理、权限控制")
@Validated
@RestController
public class TeamMemberRest {

  @Resource
  private TeamMemberFacade teamMemberFacade;

  @Operation(summary = "邀请成员", description = "邀请新成员加入团队",
      operationId = "team:invite")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "邀请已发送")
  })
  @PostMapping("/api/v1/team/members/invite")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<TeamInvitationVo> invite(
      @Valid @RequestBody MemberInviteDto dto) {
    return ApiLocaleResult.success(teamMemberFacade.invite(dto));
  }

  @Operation(summary = "更新成员角色", description = "修改团队成员角色",
      operationId = "team:updateRole")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/api/v1/team/members/{id}/role")
  public ApiLocaleResult<TeamMemberVo> updateRole(
      @PathVariable Long id, @Valid @RequestBody MemberRoleUpdateDto dto) {
    return ApiLocaleResult.success(teamMemberFacade.updateRole(id, dto));
  }

  @Operation(summary = "移除成员", description = "从团队中移除成员",
      operationId = "team:removeMember")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "移除成功")
  })
  @DeleteMapping("/api/v1/team/members/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeMember(@PathVariable Long id) {
    teamMemberFacade.removeMember(id);
  }

  @Operation(summary = "查询成员详情", description = "获取团队成员详细信息",
      operationId = "team:getMemberById")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "成员不存在")
  })
  @GetMapping("/api/v1/team/members/{id}")
  public ApiLocaleResult<TeamMemberVo> getMemberById(@PathVariable Long id) {
    return ApiLocaleResult.success(teamMemberFacade.getMemberById(id));
  }

  @Operation(summary = "查询成员列表", description = "分页查询团队成员列表",
      operationId = "team:listMembers")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/api/v1/team/members")
  public ApiLocaleResult<PageResult<TeamMemberVo>> listMembers(
      @Valid @ParameterObject TeamMemberFindDto dto) {
    return ApiLocaleResult.success(teamMemberFacade.listMembers(dto));
  }

  @Operation(summary = "查询团队统计", description = "获取团队统计数据",
      operationId = "team:getStatistics")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/api/v1/team/statistics")
  public ApiLocaleResult<TeamStatisticsVo> getStatistics() {
    return ApiLocaleResult.success(teamMemberFacade.getStatistics());
  }

  @Operation(summary = "查询邀请列表", description = "查询团队邀请列表",
      operationId = "team:listInvitations")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/api/v1/team/invitations")
  public ApiLocaleResult<PageResult<TeamInvitationVo>> listInvitations(
      @Valid @ParameterObject TeamMemberFindDto dto) {
    return ApiLocaleResult.success(teamMemberFacade.listInvitations(dto));
  }

  @Operation(summary = "重发邀请", description = "重新发送邀请邮件",
      operationId = "team:resendInvitation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "邀请已重发")
  })
  @PostMapping("/api/v1/team/invitations/{id}/resend")
  public ApiLocaleResult<TeamInvitationVo> resendInvitation(@PathVariable Long id) {
    return ApiLocaleResult.success(teamMemberFacade.resendInvitation(id));
  }

  @Operation(summary = "取消邀请", description = "取消待处理的邀请",
      operationId = "team:revokeInvitation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "取消成功")
  })
  @DeleteMapping("/api/v1/team/invitations/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void revokeInvitation(@PathVariable Long id) {
    teamMemberFacade.revokeInvitation(id);
  }

  @Operation(summary = "接受邀请", description = "接受团队邀请（公开接口）",
      operationId = "team:acceptInvitation")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "接受成功")
  })
  @PostMapping("/api/v1/public/invitations/{token}/accept")
  public ApiLocaleResult<InvitationAcceptResultVo> acceptInvitation(
      @PathVariable String token, @Valid @RequestBody InvitationAcceptDto dto) {
    return ApiLocaleResult.success(teamMemberFacade.acceptInvitation(token, dto));
  }
}
