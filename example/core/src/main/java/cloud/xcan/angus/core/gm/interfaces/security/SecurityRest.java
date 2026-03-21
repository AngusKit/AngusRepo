package cloud.xcan.angus.core.gm.interfaces.security;

import cloud.xcan.angus.core.gm.interfaces.security.facade.SecurityFacade;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.IpWhitelistCreateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.IpWhitelistUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.LoginSecurityConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.PasswordPolicyUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityAuditStatsFindDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.SecurityNotificationConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.IpWhitelistVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.LoginSecurityConfigVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.PasswordPolicyVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityAuditStatsVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityNotificationConfigVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Security", description = "安全设置 - 密码策略、登录限制、IP白名单、安全审计")
@Validated
@RestController
@RequestMapping("/api/v1/security")
public class SecurityRest {

  @Resource
  private SecurityFacade securityFacade;

  @Operation(operationId = "getPasswordPolicy", summary = "获取密码策略配置", description = "获取密码策略配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/password-policy")
  public ApiLocaleResult<PasswordPolicyVo> getPasswordPolicy() {
    return ApiLocaleResult.success(securityFacade.getPasswordPolicy());
  }

  @Operation(operationId = "updatePasswordPolicy", summary = "更新密码策略配置", description = "更新密码策略配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/password-policy")
  public ApiLocaleResult<PasswordPolicyVo> updatePasswordPolicy(
      @Valid @RequestBody PasswordPolicyUpdateDto dto) {
    return ApiLocaleResult.success(securityFacade.updatePasswordPolicy(dto));
  }

  @Operation(operationId = "getLoginSecurityConfig", summary = "获取登录安全配置", description = "获取登录安全配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/login-security")
  public ApiLocaleResult<LoginSecurityConfigVo> getLoginSecurityConfig() {
    return ApiLocaleResult.success(securityFacade.getLoginSecurityConfig());
  }

  @Operation(operationId = "updateLoginSecurityConfig", summary = "更新获取登录安全配置", description = "更新获取登录安全配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/login-security")
  public ApiLocaleResult<LoginSecurityConfigVo> updateLoginSecurityConfig(
      @Valid @RequestBody LoginSecurityConfigUpdateDto dto) {
    return ApiLocaleResult.success(securityFacade.updateLoginSecurityConfig(dto));
  }

  @Operation(operationId = "updateIpWhitelist", summary = "更新IP白名单", description = "更新IP白名单条目")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/ip-whitelist/{id}")
  public ApiLocaleResult<IpWhitelistVo> updateIpWhitelist(
      @Parameter(description = "白名单ID") @PathVariable Long id,
      @Valid @RequestBody IpWhitelistUpdateDto dto) {
    return ApiLocaleResult.success(securityFacade.updateIpWhitelist(id, dto));
  }

  @Operation(operationId = "getIpWhitelist", summary = "获取IP白名单列表", description = "分页获取IP白名单列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/ip-whitelist")
  public ApiLocaleResult<List<IpWhitelistVo>> listIpWhitelist() {
    return ApiLocaleResult.success(securityFacade.listIpWhitelist());
  }

  @Operation(operationId = "addIpWhitelist", summary = "添加IP白名单", description = "添加IP白名单条目")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "添加成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/ip-whitelist")
  public ApiLocaleResult<IpWhitelistVo> addIpWhitelist(
      @Valid @RequestBody IpWhitelistCreateDto dto) {
    return ApiLocaleResult.success(securityFacade.addIpWhitelist(dto));
  }

  @Operation(operationId = "deleteIpWhitelist", summary = "删除IP白名单", description = "删除IP白名单条目")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/ip-whitelist/{id}")
  public void deleteIpWhitelist(
      @Parameter(description = "白名单ID") @PathVariable Long id) {
    securityFacade.deleteIpWhitelist(id);
  }

  @Operation(operationId = "getSecurityAuditStats", summary = "获取安全审计统计", description = "获取安全审计统计数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/audit-stats")
  public ApiLocaleResult<SecurityAuditStatsVo> getAuditStats(
      @ParameterObject SecurityAuditStatsFindDto dto) {
    return ApiLocaleResult.success(securityFacade.getAuditStats(dto));
  }

  @Operation(operationId = "getNotificationConfig", summary = "获取安全通知配置", description = "获取安全通知配置，包括用户关键操作、系统负载、服务组件异常、登录失败、新用户注册等通知选项及接收用户列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/notification-config")
  public ApiLocaleResult<SecurityNotificationConfigVo> getNotificationConfig() {
    return ApiLocaleResult.success(securityFacade.getNotificationConfig());
  }

  @Operation(operationId = "updateNotificationConfig", summary = "更新安全通知配置", description = "更新安全通知配置，配置项包括：0-用户关键操作触发邮件通知；1-系统负载过高时通知（资源使用率超过85%）；2-系统服务组件状态异常时通知；3-用户登录失败时通知；4-新用户注册成功通知；5-通知接收用户ID列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/notification-config")
  public ApiLocaleResult<SecurityNotificationConfigVo> updateNotificationConfig(
      @Valid @RequestBody SecurityNotificationConfigUpdateDto dto) {
    return ApiLocaleResult.success(securityFacade.updateNotificationConfig(dto));
  }
}
