package cloud.xcan.angus.core.repo.interfaces.user;

import cloud.xcan.angus.core.repo.interfaces.user.facade.UserProfileFacade;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.ApiTokenCreateDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.NotificationSettingsDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.PasswordChangeDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.UserPreferencesUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.dto.UserProfileUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.ApiTokenVo;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.AvatarUploadResultVo;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.PasswordChangeResultVo;
import cloud.xcan.angus.core.repo.interfaces.user.facade.vo.UserProfileVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "UserProfile", description = "用户个人设置 - 个人信息、偏好、API Token管理")
@Validated
@RestController
@RequestMapping("/api/v1/users")
public class UserProfileRest {

  @Resource
  private UserProfileFacade userProfileFacade;

  @Operation(summary = "获取个人信息", description = "获取当前用户个人信息",
      operationId = "user:getProfile")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/profile")
  public ApiLocaleResult<UserProfileVo> getProfile() {
    return ApiLocaleResult.success(userProfileFacade.getProfile());
  }

  @Operation(summary = "更新个人信息", description = "更新当前用户个人信息",
      operationId = "user:updateProfile")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/profile")
  public ApiLocaleResult<UserProfileVo> updateProfile(
      @Valid @RequestBody UserProfileUpdateDto dto) {
    return ApiLocaleResult.success(userProfileFacade.updateProfile(dto));
  }

  @Operation(summary = "修改密码", description = "修改当前用户密码",
      operationId = "user:changePassword")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "修改成功")
  })
  @PutMapping("/password")
  public ApiLocaleResult<PasswordChangeResultVo> changePassword(
      @Valid @RequestBody PasswordChangeDto dto) {
    return ApiLocaleResult.success(userProfileFacade.changePassword(dto));
  }

  @Operation(summary = "更新偏好设置", description = "更新当前用户偏好设置",
      operationId = "user:updatePreferences")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/preferences")
  public ApiLocaleResult<UserProfileVo> updatePreferences(
      @Valid @RequestBody UserPreferencesUpdateDto dto) {
    return ApiLocaleResult.success(userProfileFacade.updatePreferences(dto));
  }

  @Operation(summary = "查询通知设置", description = "获取当前用户通知设置",
      operationId = "user:getNotificationSettings")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/notification-settings")
  public ApiLocaleResult<UserProfileVo> getNotificationSettings() {
    return ApiLocaleResult.success(userProfileFacade.getNotificationSettings());
  }

  @Operation(summary = "更新通知设置", description = "更新当前用户通知设置",
      operationId = "user:updateNotificationSettings")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/notification-settings")
  public ApiLocaleResult<UserProfileVo> updateNotificationSettings(
      @Valid @RequestBody NotificationSettingsDto dto) {
    return ApiLocaleResult.success(userProfileFacade.updateNotificationSettings(dto));
  }

  @Operation(summary = "创建API Token", description = "为当前用户创建API Token",
      operationId = "user:createApiToken")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功")
  })
  @PostMapping("/api-tokens")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<ApiTokenVo> createApiToken(
      @Valid @RequestBody ApiTokenCreateDto dto) {
    return ApiLocaleResult.success(userProfileFacade.createApiToken(dto));
  }

  @Operation(summary = "查询API Token列表", description = "获取当前用户所有API Token",
      operationId = "user:listApiTokens")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/api-tokens")
  public ApiLocaleResult<List<ApiTokenVo>> listApiTokens() {
    return ApiLocaleResult.success(userProfileFacade.listApiTokens());
  }

  @Operation(summary = "撤销API Token", description = "撤销指定的API Token",
      operationId = "user:revokeApiToken")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "撤销成功")
  })
  @DeleteMapping("/api-tokens/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void revokeApiToken(@PathVariable Long id) {
    userProfileFacade.revokeApiToken(id);
  }

  @Operation(summary = "上传头像", description = "上传当前用户头像",
      operationId = "user:uploadAvatar")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "上传成功")
  })
  @PostMapping("/avatar")
  public ApiLocaleResult<AvatarUploadResultVo> uploadAvatar(
      @RequestParam("file") MultipartFile file) throws IOException {
    return ApiLocaleResult.success(
        userProfileFacade.uploadAvatar(file.getOriginalFilename(), file.getBytes()));
  }
}
