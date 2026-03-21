package cloud.xcan.angus.core.gm.interfaces.user;

import cloud.xcan.angus.core.gm.interfaces.user.facade.UserSettingFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.BatchUpdateNotificationDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UpdateAppearanceDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UpdateNotificationDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.AppearancePreferencesVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.LanguageVo;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.NotificationPreferencesVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserSetting", description = "用户设置 - 外观偏好、通知偏好、安全配置等设置的查询和更新")
@Validated
@RestController
@RequestMapping("/api/v1/user/settings")
public class UserSettingRest {

  @Resource
  private UserSettingFacade userSettingFacade;

  @Operation(operationId = "updateAppearancePreferences", summary = "更新外观偏好设置",
      description = "更新当前用户的外观偏好设置（主题、语言、字体大小）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/appearance")
  public ApiLocaleResult<AppearancePreferencesVo> updateAppearance(
      @Valid @RequestBody UpdateAppearanceDto dto) {
    return ApiLocaleResult.success(userSettingFacade.updateAppearance(dto));
  }

  @Operation(operationId = "getAppearancePreferences", summary = "获取外观偏好设置详情",
      description = "获取当前用户的外观偏好设置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/appearance")
  public ApiLocaleResult<AppearancePreferencesVo> getAppearance() {
    return ApiLocaleResult.success(userSettingFacade.getAppearance());
  }

  @Operation(operationId = "getSupportedLanguages", summary = "获取支持的语言列表",
      description = "获取系统支持的语言列表（仅返回已启用的语言）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/appearance/languages")
  public ApiLocaleResult<List<LanguageVo>> getSupportedLanguages() {
    return ApiLocaleResult.success(userSettingFacade.getSupportedLanguages());
  }

  @Operation(operationId = "updateNotificationPreferences", summary = "更新通知偏好设置",
      description = "更新当前用户的通知偏好设置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/notification")
  public ApiLocaleResult<NotificationPreferencesVo> updateNotification(
      @Valid @RequestBody UpdateNotificationDto dto) {
    return ApiLocaleResult.success(userSettingFacade.updateNotification(dto));
  }

  @Operation(operationId = "getNotificationPreferences", summary = "获取通知偏好设置详情",
      description = "获取当前用户的通知偏好设置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/notification")
  public ApiLocaleResult<NotificationPreferencesVo> getNotification() {
    return ApiLocaleResult.success(userSettingFacade.getNotification());
  }

  @Operation(operationId = "batchUpdateNotification", summary = "批量更新通知类型设置",
      description = "批量更新特定类型的通知设置（如一键关闭所有邮件通知）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "批量更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/notification/batch")
  public ApiLocaleResult<NotificationPreferencesVo> batchUpdateNotification(
      @Valid @RequestBody BatchUpdateNotificationDto dto) {
    return ApiLocaleResult.success(userSettingFacade.batchUpdateNotification(dto));
  }

}
