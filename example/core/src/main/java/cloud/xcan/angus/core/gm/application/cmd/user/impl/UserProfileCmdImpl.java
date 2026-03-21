package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.core.utils.CoreUtils.copyPropertiesIgnoreNull;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserId;

import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserRepo;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserProfileCmd;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileCmdImpl implements UserProfileCmd {

  @Resource
  private UserRepo userRepo;

  @Resource
  private UserQuery userQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public User updateProfile(User user) {
    return new BizTemplate<User>() {
      User existing;

      @Override
      protected void checkParams() {
        // 验证用户存在
        existing = userQuery.findAndCheck(user.getId());
        // 验证只能修改自己的信息
        Long currentUserId = getUserId();
        if (!user.getId().equals(currentUserId)) {
          throw ProtocolException.of("只能修改自己的个人信息");
        }
      }

      @Override
      protected User process() {
        // 使用空值安全的属性复制更新用户信息
        copyPropertiesIgnoreNull(user, existing);
        User saved = userRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            saved.getId(),
            saved.getName(),
            OperationMessage.USER_UPDATE_PROFILE_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public User updateAvatar(Long userId, String avatarUrl) {
    return new BizTemplate<User>() {
      User existing;

      @Override
      protected void checkParams() {
        // 验证用户存在
        existing = userQuery.findAndCheck(userId);
        // 验证只能修改自己的信息
        Long currentUserId = getUserId();
        if (!userId.equals(currentUserId)) {
          throw ProtocolException.of("只能修改自己的头像");
        }
      }

      @Override
      protected User process() {
        // 更新头像URL
        existing.setAvatar(avatarUrl);
        User saved = userRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            saved.getId(),
            saved.getName(),
            OperationMessage.USER_UPDATE_AVATAR_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public User deleteAvatar(Long userId) {
    return new BizTemplate<User>() {
      User existing;

      @Override
      protected void checkParams() {
        // 验证用户存在
        existing = userQuery.findAndCheck(userId);
        // 验证只能修改自己的信息
        Long currentUserId = getUserId();
        if (!userId.equals(currentUserId)) {
          throw ProtocolException.of("只能删除自己的头像");
        }
      }

      @Override
      protected User process() {
        // 删除头像（设置为null）
        existing.setAvatar(null);
        User saved = userRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.USER,
            saved.getId(),
            saved.getName(),
            OperationMessage.USER_DELETE_AVATAR_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }
}
