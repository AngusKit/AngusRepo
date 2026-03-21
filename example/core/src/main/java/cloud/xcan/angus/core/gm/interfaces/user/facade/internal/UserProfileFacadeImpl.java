package cloud.xcan.angus.core.gm.interfaces.user.facade.internal;

import static cloud.xcan.angus.core.gm.domain.CommonConstant.MAX_AVATAR_SIZE;
import static cloud.xcan.angus.core.gm.domain.CommonConstant.UPLOAD_AVATAR_KEY;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserProfileAssembler.toUpdateDomain;
import static cloud.xcan.angus.core.gm.interfaces.user.facade.internal.assembler.UserProfileAssembler.toVo;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.storage.file.FileRemote;
import cloud.xcan.angus.api.storage.file.vo.FileUploadVo;
import cloud.xcan.angus.core.gm.application.cmd.user.UserProfileCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserProfileQuery;
import cloud.xcan.angus.core.gm.interfaces.user.facade.UserProfileFacade;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserAvatarUploadDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.dto.UserProfileUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.user.facade.vo.UserProfileVo;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UserProfileFacadeImpl implements UserProfileFacade {

  @Resource
  private UserProfileCmd userProfileCmd;

  @Resource
  private UserProfileQuery userProfileQuery;

  @Resource
  private FileRemote fileRemote;

  @Override
  public UserProfileVo updateProfile(UserProfileUpdateDto dto) {
    User user = toUpdateDomain(dto);
    User updated = userProfileCmd.updateProfile(user);
    return toVo(updated);
  }

  @Override
  public UserProfileVo getProfile() {
    User user = userProfileQuery.findByUserId(getUserId());
    return toVo(user);
  }

  @Override
  public UserProfileVo uploadAvatar(UserAvatarUploadDto dto) {
    validateAvatarFile(dto.getFile());
    List<FileUploadVo> uploadVos = fileRemote.upload(
            new MultipartFile[]{dto.getFile()},
            null, UPLOAD_AVATAR_KEY, null, null, null)
        .orElseContentThrow();
    User updated = userProfileCmd.updateAvatar(getUserId(), uploadVos.get(0).getUrl());
    return toVo(updated);
  }

  @Override
  public String uploadAvatarUrl(UserAvatarUploadDto dto) {
    validateAvatarFile(dto.getFile());
    List<FileUploadVo> uploadVos = fileRemote.upload(
            new MultipartFile[]{dto.getFile()},
            null, UPLOAD_AVATAR_KEY, null, null, null)
        .orElseContentThrow();
    return uploadVos.get(0).getUrl();
  }

  @Override
  public void deleteAvatar() {
    userProfileCmd.deleteAvatar(getUserId());
  }

  private void validateAvatarFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw ProtocolException.of("头像文件不能为空");
    }
    // 验证文件类型
    String contentType = file.getContentType();
    if (!contentType.equals("image/jpeg") && !contentType.equals("image/jpg")
        && !contentType.equals("image/png")) {
      throw ProtocolException.of("头像文件格式不支持，仅支持jpg、png、jpeg格式");
    }
    // 验证文件大小（5MB）
    if (file.getSize() > MAX_AVATAR_SIZE) {
      throw ProtocolException.of("头像文件大小超出限制，最大5MB");
    }
  }

}
