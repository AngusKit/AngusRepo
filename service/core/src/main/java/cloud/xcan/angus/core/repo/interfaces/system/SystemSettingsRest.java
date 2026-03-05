package cloud.xcan.angus.core.repo.interfaces.system;

import cloud.xcan.angus.core.repo.interfaces.system.facade.SystemSettingsFacade;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.AuthSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.ConnectionTestDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.GeneralSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.IntegrationSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.LicenseUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.StorageSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.SystemRestartDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.AuthSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.ConnectionTestResultVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.GeneralSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.IntegrationSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.LicenseInfoVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.StorageSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.SystemRestartResultVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.SystemSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.SystemStatusVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SystemSettings", description = "系统设置 - 通用设置、存储、认证、集成配置管理")
@Validated
@RestController
@RequestMapping("/api/v1/system")
public class SystemSettingsRest {

  @Resource
  private SystemSettingsFacade systemSettingsFacade;

  @Operation(summary = "查询系统设置", description = "获取所有系统设置",
      operationId = "system:getSettings")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/settings")
  public ApiLocaleResult<SystemSettingsVo> getSettings() {
    return ApiLocaleResult.success(systemSettingsFacade.getSettings());
  }

  @Operation(summary = "更新通用设置", description = "更新系统通用设置",
      operationId = "system:updateGeneral")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/settings/general")
  public ApiLocaleResult<GeneralSettingsVo> updateGeneralSettings(
      @Valid @RequestBody GeneralSettingsUpdateDto dto) {
    return ApiLocaleResult.success(systemSettingsFacade.updateGeneralSettings(dto));
  }

  @Operation(summary = "更新存储设置", description = "更新存储后端配置",
      operationId = "system:updateStorage")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/settings/storage")
  public ApiLocaleResult<StorageSettingsVo> updateStorageSettings(
      @Valid @RequestBody StorageSettingsUpdateDto dto) {
    return ApiLocaleResult.success(systemSettingsFacade.updateStorageSettings(dto));
  }

  @Operation(summary = "更新认证设置", description = "更新认证和安全配置",
      operationId = "system:updateAuth")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/settings/authentication")
  public ApiLocaleResult<AuthSettingsVo> updateAuthSettings(
      @Valid @RequestBody AuthSettingsUpdateDto dto) {
    return ApiLocaleResult.success(systemSettingsFacade.updateAuthSettings(dto));
  }

  @Operation(summary = "更新集成设置", description = "更新外部系统集成配置",
      operationId = "system:updateIntegrations")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/settings/integrations")
  public ApiLocaleResult<IntegrationSettingsVo> updateIntegrationSettings(
      @Valid @RequestBody IntegrationSettingsUpdateDto dto) {
    return ApiLocaleResult.success(systemSettingsFacade.updateIntegrationSettings(dto));
  }

  @Operation(summary = "测试连接", description = "测试外部服务连接",
      operationId = "system:testConnection")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "测试完成")
  })
  @PostMapping("/settings/test-connection")
  public ApiLocaleResult<ConnectionTestResultVo> testConnection(
      @Valid @RequestBody ConnectionTestDto dto) {
    return ApiLocaleResult.success(systemSettingsFacade.testConnection(dto));
  }

  @Operation(summary = "查询系统状态", description = "获取系统运行状态",
      operationId = "system:getStatus")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/status")
  public ApiLocaleResult<SystemStatusVo> getStatus() {
    return ApiLocaleResult.success(systemSettingsFacade.getSystemStatus());
  }

  @Operation(summary = "查询许可证信息", description = "获取系统许可证信息",
      operationId = "system:getLicense")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/license")
  public ApiLocaleResult<LicenseInfoVo> getLicense() {
    return ApiLocaleResult.success(systemSettingsFacade.getLicense());
  }

  @Operation(summary = "更新许可证", description = "更新系统许可证",
      operationId = "system:updateLicense")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/license")
  public ApiLocaleResult<LicenseInfoVo> updateLicense(
      @Valid @RequestBody LicenseUpdateDto dto) {
    return ApiLocaleResult.success(systemSettingsFacade.updateLicense(dto));
  }

  @Operation(summary = "系统重启/维护", description = "请求系统重启或进入维护模式",
      operationId = "system:restart")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "操作成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/restart")
  public ApiLocaleResult<SystemRestartResultVo> restart(
      @Valid @RequestBody(required = false) SystemRestartDto dto) {
    return ApiLocaleResult.success(systemSettingsFacade.restart(dto));
  }
}
