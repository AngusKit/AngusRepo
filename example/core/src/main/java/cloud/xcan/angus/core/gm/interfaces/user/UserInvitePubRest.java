package cloud.xcan.angus.core.gm.interfaces.user;

import cloud.xcan.angus.api.gm.user.vo.UserInviteVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserInviteFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserAcceptInviteDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserRejectInviteDto;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserInvitePub", description = "用户邀请管理 - 查询邀请信息和接收邀请接口")
@Validated
@RestController
@RequestMapping("/pubapi/v1/user/invites")
public class UserInvitePubRest {

  @Resource
  private UserInviteFacade userInviteFacade;

  @Operation(operationId = "getInviteByCode", summary = "根据邀请码查询邀请信息",
      description = "根据邀请码查询邀请信息，返回邀请的基本信息（不包含敏感信息）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{inviteCode}")
  public ApiLocaleResult<UserInviteVo> getInviteByCode(
      @Parameter(description = "邀请码", required = true) @PathVariable String inviteCode) {
    return ApiLocaleResult.success(userInviteFacade.getInviteByCode(inviteCode));
  }

  @Operation(operationId = "acceptInvite", summary = "接收邀请并创建账号",
      description = "使用邀请码注册账号，邀请码必须有效且未过期")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "注册成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/accept")
  public ApiLocaleResult<UserDetailVo> acceptInvite(@Valid @RequestBody UserAcceptInviteDto dto) {
    return ApiLocaleResult.success(userInviteFacade.acceptInvite(dto));
  }

  @Operation(operationId = "rejectInvite", summary = "拒绝邀请",
      description = "被邀请人拒绝邀请，邀请码必须有效且状态为待接受")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "拒绝成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/reject")
  public ApiLocaleResult<?> rejectInvite(@Valid @RequestBody UserRejectInviteDto dto) {
    userInviteFacade.rejectInviteByCode(dto.getInviteCode());
    return ApiLocaleResult.success();
  }

}
