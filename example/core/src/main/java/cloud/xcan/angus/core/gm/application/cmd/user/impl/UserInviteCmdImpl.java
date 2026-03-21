package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.api.commonlink.GMConstant.GM_APP_CODE;
import static cloud.xcan.angus.api.commonlink.GMConstant.USER_EMAIL_INVITATION_TEMPLATE_CODE;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.daysBetween;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getUserFullName;
import static cloud.xcan.angus.spec.utils.DateUtils.asDate;
import static cloud.xcan.angus.spec.utils.DateUtils.formatByDatePattern;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.department.Department;
import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.api.commonlink.user.enums.InviteStatus;
import cloud.xcan.angus.api.commonlink.user.enums.InviteType;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserInviteCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.department.DepartmentQuery;
import cloud.xcan.angus.core.gm.application.query.role.RoleQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserInviteQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.user.UserInvite;
import cloud.xcan.angus.core.gm.domain.user.UserInviteRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.core.utils.PrincipalContextUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserInviteCmdImpl extends CommCmd<UserInvite, Long> implements UserInviteCmd {

  @Resource
  private UserInviteRepo userInviteRepo;

  @Resource
  private UserInviteQuery userInviteQuery;

  @Resource
  private ApplicationQuery applicationQuery;

  @Resource
  private DepartmentQuery departmentQuery;

  @Resource
  private RoleQuery roleQuery;

  @Resource
  private EmailCmd emailCmd;

  @Resource
  private UserQuery userQuery;

  @Resource
  private ApplicationInfo applicationInfo;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserInvite create(UserInvite userInvite) {
    return new BizTemplate<UserInvite>() {
      Application application;
      Department department;
      Role role;

      @Override
      protected void checkParams() {
        if (userInvite.getInviteType().isEmail()) {
          // 检查邮件是否为空
          assertNotEmpty(userInvite.getEmail(), "邮件邀请时邮箱不能为空");

          // 检查邮箱是否已被邀请（待处理状态）
          if (userInviteRepo.existsByEmailAndStatus(userInvite.getEmail(), InviteStatus.PENDING)) {
            throw ResourceExisted.of("邮箱「{0}」已有待处理的邀请",
                new Object[]{userInvite.getEmail()});
          }

          // 检查邮箱是否已注册用户
          if (userQuery.existsByEmail(userInvite.getEmail())) {
            throw ResourceExisted.of("邮箱「{0}」已注册用户",
                new Object[]{userInvite.getEmail()});
          }
        }

        // 检查邀请应用是否存在
        if (userInvite.getAppId() != null) {
          application = applicationQuery.findAndCheck(userInvite.getAppId());
          PrincipalContextUtils.setMultiTenantCtrl(true);
        }
        // 检查邀请部门是否存在
        if (userInvite.getDepartmentId() != null) {
          department = departmentQuery.findAndCheck(userInvite.getDepartmentId());
        }
        // 检查邀请角色是否存在
        if (userInvite.getRoleId() != null) {
          role = roleQuery.findAndCheck(userInvite.getRoleId());
        }
      }

      @Override
      protected UserInvite process() {
        // 生成邀请码
        String inviteCode = UUID.randomUUID().toString().replace("-", "");
        userInvite.setInviteCode(inviteCode);

        // 设置状态
        if (userInvite.getStatus() == null) {
          userInvite.setStatus(InviteStatus.PENDING);
        }

        // 设置邀请时间
        if (userInvite.getInviteDate() == null) {
          userInvite.setInviteDate(LocalDateTime.now());
        }

        // 处理邀请
        doInvite(userInvite, application, department, role);

        UserInvite saved = insert(userInvite);

        // 记录操作日志
        String inviteIdentifier = userInvite.getInviteType().isEmail()
            ? userInvite.getEmail()
            : "链接邀请";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.USER,
            saved.getId(),
            inviteIdentifier,
            OperationMessage.USER_INVITE_CREATE_DETAILS,
            new Object[]{inviteIdentifier}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void cancel(Long id) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        userInviteQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        UserInvite userInvite = userInviteQuery.findAndCheck(id);
        String inviteIdentifier = userInvite.getInviteType().isEmail()
            ? userInvite.getEmail()
            : "链接邀请";
        userInvite.setStatus(InviteStatus.CANCELLED);
        userInviteRepo.save(userInvite);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            id,
            inviteIdentifier,
            OperationMessage.USER_INVITE_CANCEL_DETAILS,
            new Object[]{inviteIdentifier}
        );

        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserInvite resend(Long id) {
    return new BizTemplate<UserInvite>() {
      UserInvite userInvite;
      Application application;
      Department department;
      Role role;

      @Override
      protected void checkParams() {
        userInvite = userInviteQuery.findAndCheck(id);
        // 检查邀请应用是否存在
        if (userInvite.getAppId() != null) {
          application = applicationQuery.findAndCheck(userInvite.getAppId());
          PrincipalContextUtils.setMultiTenantCtrl(true);
        }
        // 检查邀请部门是否存在
        if (userInvite.getDepartmentId() != null) {
          department = departmentQuery.findAndCheck(userInvite.getDepartmentId());
        }
        // 检查邀请角色是否存在
        if (userInvite.getRoleId() != null) {
          role = roleQuery.findAndCheck(userInvite.getRoleId());
        }
      }

      @Override
      protected UserInvite process() {
        // 生成邀请码
        String inviteCode = UUID.randomUUID().toString().replace("-", "");
        userInvite.setInviteCode(inviteCode);

        // 更新邀请时间
        userInvite.setInviteDate(LocalDateTime.now());

        // 如果已过期，更新过期时间
        if (userInvite.getExpiryDate() != null && userInvite.getExpiryDate()
            .isBefore(LocalDateTime.now())) {
          userInvite.setExpiryDate(LocalDateTime.now().plusDays(7));
        }

        // 重置状态为待处理
        userInvite.setStatus(InviteStatus.PENDING);

        // 处理邀请
        doInvite(userInvite, application, department, role);

        UserInvite saved = userInviteRepo.save(userInvite);

        // 记录操作日志
        String inviteIdentifier = userInvite.getInviteType().isEmail()
            ? userInvite.getEmail()
            : "链接邀请";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            saved.getId(),
            inviteIdentifier,
            OperationMessage.USER_INVITE_RESEND_DETAILS,
            new Object[]{inviteIdentifier}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  public void update0(UserInvite userInvite) {
    userInviteRepo.save(userInvite);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void reject(String inviteCode) {
    new BizTemplate<Void>() {
      @Override
      protected void checkParams() {
        UserInvite userInvite = userInviteQuery.findByInviteCode(inviteCode)
            .orElseThrow(() -> ResourceNotFound.of("邀请码「{0}」不存在", new Object[]{inviteCode}));
        if (userInvite.getStatus() != InviteStatus.PENDING) {
          throw ProtocolException.of("邀请码「{0}」状态无效，无法拒绝", new Object[]{inviteCode});
        }
      }

      @Override
      protected Void process() {
        UserInvite userInvite = userInviteQuery.findByInviteCode(inviteCode).orElseThrow();
        String inviteIdentifier = userInvite.getInviteType().isEmail()
            ? userInvite.getEmail()
            : "链接邀请";
        userInvite.setStatus(InviteStatus.REJECTED);
        userInviteRepo.save(userInvite);

        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            userInvite.getId(),
            inviteIdentifier,
            OperationMessage.USER_INVITE_REJECT_DETAILS,
            new Object[]{inviteIdentifier}
        );

        return null;
      }
    }.execute();
  }

  private void doInvite(UserInvite userInvite, Application application,
      Department department, Role role) {
    // 查询处理邀请链接应用
    Application inviteApp = applicationQuery.findByCodeAndEditionType(
            GM_APP_CODE, applicationInfo.getEditionType())
        .orElseThrow(() -> ResourceNotFound.of("应用「{0}-{1}」不存在",
            new Object[]{GM_APP_CODE, applicationInfo.getEditionType()}));
    if (isEmpty(inviteApp.getUrl())) {
      throw SysException.of("应用「{0}-{1}」站点URL未配置",
          new Object[]{GM_APP_CODE, applicationInfo.getEditionType()});
    }

    // 根据邀请方式处理不同的逻辑
    if (userInvite.getInviteType() == InviteType.LINK) {
      // 设置用户链接邀请接收页面
      userInvite.setInviteUrl(inviteApp.getUrl()
          + "/invite-link-signup?inviteCode=" + userInvite.getInviteCode());
    } else if (userInvite.getInviteType() == InviteType.EMAIL) {
      // 设置用户邮件邀请接收页面
      userInvite.setInviteUrl(inviteApp.getUrl()
          + "/invite-email-signup?inviteCode=" + userInvite.getInviteCode());
      // 发送邀请邮件
      Map<String, String> params = getUserInviteTemplateParams(userInvite, application,
          department, role);
      emailCmd.sendByTemplate(USER_EMAIL_INVITATION_TEMPLATE_CODE, null,
          userInvite.getEmail(), null, null, params, false);
    }
  }

  private static @NotNull Map<String, String> getUserInviteTemplateParams(
      UserInvite userInvite, Application application, Department department, Role role) {
    Map<String, String> params = new HashMap<>();
    params.put("inviteLink", userInvite.getInviteUrl());
    params.put("inviteCode", userInvite.getInviteCode());
    params.put("inviteType", userInvite.getInviteType().name());
    params.put("inviteDate", formatByDatePattern(asDate(userInvite.getInviteDate())));
    if (userInvite.getExpiryDate() != null) {
      params.put("expiryDays", String.valueOf(
          daysBetween(LocalDateTime.now().minusMinutes(1), userInvite.getExpiryDate())));
    } else {
      params.put("expiryDays", "--");
    }
    params.put("inviterName", getUserFullName());
    if (application != null) {
      params.put("application", application.getCode());
    } else {
      params.put("application", "--");
    }
    if (department != null) {
      params.put("teamName", department.getName());
    } else {
      params.put("teamName", "--");
    }
    if (role != null) {
      params.put("role", role.getName());
    } else {
      params.put("role", "--");
    }
    return params;
  }

  @Override
  protected BaseRepository<UserInvite, Long> getRepository() {
    return userInviteRepo;
  }
}
