package cloud.xcan.angus.core.gm.interfaces.user;

import cloud.xcan.angus.core.gm.interfaces.user.facade.UserProfileFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserAvatarUploadDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserProfileUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserProfileVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "UserProfile", description = "用户个人信息管理 - 查询和更新用户个人资料")
@Validated
@RestController
@RequestMapping("/api/v1/user/profile")
public class UserProfileRest {

  @Resource
  private UserProfileFacade userProfileFacade;

  @Operation(operationId = "updateUserProfile", summary = "更新个人信息",
      description = "更新当前登录用户的个人资料信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "个人信息更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping
  public ApiLocaleResult<UserProfileVo> updateProfile(
      @Valid @RequestBody UserProfileUpdateDto dto) {
    return ApiLocaleResult.success(userProfileFacade.updateProfile(dto));
  }

  @Operation(operationId = "getUserProfile", summary = "获取个人信息详情",
      description = "获取当前登录用户的个人资料详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "个人信息获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<UserProfileVo> getProfile() {
    return ApiLocaleResult.success(userProfileFacade.getProfile());
  }

  @Operation(operationId = "uploadAvatar", summary = "上传头像",
      description = "上传并更新用户头像（支持jpg、png、jpeg，最大5MB）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "头像上传成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiLocaleResult<UserProfileVo> uploadAvatar(
      @Parameter(description = "文件", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE),
          schema = @Schema(type = "object")) UserAvatarUploadDto dto) {
    return ApiLocaleResult.success(userProfileFacade.uploadAvatar(dto));
  }

  @Operation(operationId = "uploadAvatarUrl", summary = "上传头像获取URL",
      description = "上传头像文件并返回URL，不更新当前用户（用于管理员编辑用户时上传头像，支持jpg、png、jpeg，最大5MB）")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "头像上传成功，返回URL")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping(value = "/avatar/url", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ApiLocaleResult<String> uploadAvatarUrl(
      @Parameter(description = "文件", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE),
          schema = @Schema(type = "object")) UserAvatarUploadDto dto) {
    return ApiLocaleResult.successData(userProfileFacade.uploadAvatarUrl(dto));
  }

  @Operation(operationId = "deleteAvatar", summary = "删除头像",
      description = "删除用户头像，恢复为默认头像")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "头像删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/avatar")
  public void deleteAvatar() {
    userProfileFacade.deleteAvatar();
  }
}
