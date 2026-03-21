package cloud.xcan.angus.api.gm.user;

import cloud.xcan.angus.api.gm.user.dto.UserInviteDto;
import cloud.xcan.angus.api.gm.user.dto.UserInviteFindDto;
import cloud.xcan.angus.api.gm.user.vo.UserInviteResendVo;
import cloud.xcan.angus.api.gm.user.vo.UserInviteVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface UserInviteRemote {

  @Operation(operationId = "inviteUser", summary = "邀请用户", description = "发送用户邀请，支持单邮箱、多邮箱批量邀请及链接邀请")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "邀请发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping(value = "/api/v1/user/invites")
  ApiLocaleResult<List<UserInviteVo>> invite(@Valid @RequestBody UserInviteDto dto);

  @Operation(operationId = "cancelInvite", summary = "取消邀请", description = "取消指定邀请")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "取消成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/api/v1/user/invites/{id}")
  void cancelInvite(@Parameter(description = "邀请ID") @PathVariable Long id);

  @Operation(operationId = "resendInvite", summary = "重新发送邀请", description = "重新发送指定邀请")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "邀请已重新发送")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/api/v1/user/invites/{id}/resend")
  ApiLocaleResult<UserInviteResendVo> resendInvite(
      @Parameter(description = "邀请ID") @PathVariable Long id);

  @Operation(operationId = "getInviteList", summary = "获取邀请列表", description = "获取用户邀请列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "邀请列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping(value = "/api/v1/user/invites")
  ApiLocaleResult<PageResult<UserInviteVo>> listInvites(
      @Valid @ParameterObject @SpringQueryMap UserInviteFindDto dto);
}
