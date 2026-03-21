package cloud.xcan.angus.core.gm.interfaces.user;

import cloud.xcan.angus.core.gm.interfaces.user.facade.UserSecurityFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.Confirm2FADto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.DevicesQueryDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.Disable2FADto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.Enable2FADto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Confirm2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Disable2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.Enable2FAVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.LoginDeviceVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserSecurityVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserSecurity", description = "用户账号安全管理 - 密码管理、双因素认证、登录设备管理")
@Validated
@RestController
@RequestMapping("/api/v1/user/security")
public class UserSecurityRest {

  @Resource
  private UserSecurityFacade userSecurityFacade;

  @Operation(operationId = "enable2FA", summary = "启用双因素认证",
      description = "为当前用户启用双因素认证（2FA）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "双因素认证启用成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/2fa/enable")
  public ApiLocaleResult<Enable2FAVo> enable2FA(@Valid @RequestBody Enable2FADto dto) {
    return ApiLocaleResult.success(userSecurityFacade.enable2FA(dto));
  }

  @Operation(operationId = "confirm2FA", summary = "确认启用双因素认证",
      description = "验证并确认启用双因素认证")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "双因素认证已启用")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/2fa/confirm")
  public ApiLocaleResult<Confirm2FAVo> confirm2FA(@Valid @RequestBody Confirm2FADto dto) {
    return ApiLocaleResult.success(userSecurityFacade.confirm2FA(dto));
  }

  @Operation(operationId = "disable2FA", summary = "禁用双因素认证",
      description = "禁用当前用户的双因素认证")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "双因素认证已禁用")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/2fa/disable")
  public ApiLocaleResult<Disable2FAVo> disable2FA(@Valid @RequestBody Disable2FADto dto) {
    return ApiLocaleResult.success(userSecurityFacade.disable2FA(dto));
  }

  @Operation(operationId = "getSecurity", summary = "获取安全设置详情",
      description = "获取当前用户的安全设置信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "安全设置获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<UserSecurityVo> getSecurity() {
    return ApiLocaleResult.success(userSecurityFacade.getSecurity());
  }

  @Operation(operationId = "listDevices", summary = "获取登录设备列表",
      description = "获取当前用户的登录设备列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "设备列表获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/devices")
  public ApiLocaleResult<PageResult<LoginDeviceVo>> listDevices(
      @Valid @ParameterObject DevicesQueryDto dto) {
    return ApiLocaleResult.success(userSecurityFacade.listDevices(dto));
  }

  @Operation(operationId = "deleteDevice", summary = "删除登录设备",
      description = "删除指定的登录设备（强制退出登录）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "设备删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/devices/{deviceId}")
  public void deleteDevice(
      @Parameter(description = "设备ID") @PathVariable Long deviceId) {
    userSecurityFacade.deleteDevice(deviceId);
  }
}
