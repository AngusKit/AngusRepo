package cloud.xcan.angus.core.gm.application.cmd.tenant.impl;

import static cloud.xcan.angus.core.gm.application.converter.UserConverter.toTenantAdminUser;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getApiType;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.QuotaConstant;
import cloud.xcan.angus.api.commonlink.application.ApplicationMenuRepo;
import cloud.xcan.angus.api.commonlink.department.DepartmentUserRepo;
import cloud.xcan.angus.api.commonlink.group.GroupUserRepo;
import cloud.xcan.angus.api.commonlink.role.RoleRepo;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.TenantRepo;
import cloud.xcan.angus.api.commonlink.tenant.enums.AccountType;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.manager.QuotaManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.department.DepartmentCmd;
import cloud.xcan.angus.core.gm.application.cmd.group.GroupCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.quota.QuotaCmd;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.query.tenant.TenantQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRepo;
import cloud.xcan.angus.core.gm.domain.authorization.AuthorizationRoleRepo;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantCmdImpl extends CommCmd<Tenant, Long> implements TenantCmd {

  @Resource
  private TenantRepo tenantRepo;

  @Resource
  private TenantQuery tenantQuery;

  @Resource
  private UserCmd userCmd;

  @Resource
  private UserQuery userQuery;

  @Resource
  private AuthorizationRoleRepo authorizationRoleRepo;

  @Resource
  private AuthorizationRepo authorizationRepo;

  @Resource
  private RoleRepo roleRepo;

  @Resource
  private ApplicationMenuRepo applicationMenuRepo;

  @Resource
  private GroupUserRepo groupUserRepo;

  @Resource
  private GroupCmd groupCmd;

  @Resource
  private DepartmentUserRepo departmentUserRepo;

  @Resource
  private DepartmentCmd departmentCmd;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Resource
  private QuotaManager quotaManager;

  @Resource
  private QuotaCmd quotaCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Tenant create(Tenant tenant) {
    return new BizTemplate<Tenant>(false) {
      List<Tenant> tenants;
      Tenant mainAccountTenant;
      boolean isAddMainAccount = false;

      @Override
      protected void checkParams() {
        // 检查租户编码是否存在
        if (tenantRepo.existsByCode(tenant.getCode())) {
          throw ResourceExisted.of("租户编码「{0}」已存在", new Object[]{tenant.getCode()});
        }

        // 如果没有租户（管理员邮箱和名称是必须的）
        tenants = tenantQuery.getSameAccountTenants();

        // 检查主账号是否一致
        isAddMainAccount = tenants.isEmpty();

        // 获取主账号
        mainAccountTenant = isAddMainAccount ? tenant : tenants.stream()
            .filter(x -> x.getAccountType().isMainAccount() && x.getId()
                .equals(tenant.getMainTenantId())).findFirst().orElse(null);
        if (mainAccountTenant == null || (tenant.getMainTenantId() != null
            && !tenant.getMainTenantId().equals(mainAccountTenant.getId()))) {
          throw ProtocolException.of("主账号ID错误或者主账号未找到");
        }

        // 验证添加主账号时必须的信息是否有效
        if (isAddMainAccount && !getApiType().isPubTypeApi()) {
          if (isEmpty(tenant.getAdminEmail())) {
            throw ProtocolException.of("主账号管理员邮箱缺失");
          }
          if (tenants.stream().allMatch(x -> tenant.getAdminEmail().equals(x.getAdminEmail()))) {
            throw ProtocolException.of("邮箱「{0}」已被占用，请更换邮箱再试",
                new Object[]{tenant.getAdminEmail()});
          }
          if (isEmpty(tenant.getAdminName())) {
            throw ProtocolException.of("主账号管理员名称缺失");
          }
        }
      }

      @Override
      protected Tenant process() {
        // 第一个租户设置为主账号
        if (isAddMainAccount) {
          tenant.setAccountType(AccountType.MAIN);
          // 根据配额模版初始化租户配额
          quotaCmd.initTenantQuotasFromTemplates(mainAccountTenant.getId());
        } else {
          tenant.setAccountType(AccountType.SUB);
          tenant.setMainTenantId(nullSafe(tenant.getMainTenantId(), getOptTenantId()));
        }

        // 增加租户配额使用量
        quotaManager.increaseTenantQuota(mainAccountTenant.getId(),
            QuotaConstant.QuotaTenantCount, 1L);

        // 保存租户
        insert(tenant);

        // 保存或更新管理员信息（排除注册用户，注册用户在注册业务中初始化用户）
        if (!getApiType().isPubTypeApi() && isNotEmpty(tenant.getAdminEmail())) {
          // 保存租户管理员
          userCmd.create0(toTenantAdminUser(tenant.getId(), tenant.getAdminName(),
              tenant.getAdminEmail(), tenant.getAdminPhone()));
        }

        // 记录操作日志
        String accountTypeText = tenant.getAccountType().isMainAccount() ? "主账号" : "子账号";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.TENANT,
            tenant.getId(),
            tenant.getName(),
            OperationMessage.TENANT_CREATE_DETAILS,
            new Object[]{tenant.getName(), accountTypeText}
        );

        return tenant;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Tenant update(Tenant tenant) {
    return new BizTemplate<Tenant>(false) {
      Tenant existing;

      @Override
      protected void checkParams() {
        // 检查租户是否存在
        existing = tenantQuery.findAndCheck(tenant.getId());

        // 检查租户编码是否存在
        if (tenant.getCode() != null && !tenant.getCode().equals(existing.getCode())) {
          if (tenantRepo.existsByCodeAndIdNot(tenant.getCode(), tenant.getId())) {
            throw ResourceExisted.of("租户编码「{0}」已存在", new Object[]{tenant.getCode()});
          }
        }
      }

      @Override
      protected Tenant process() {
        // 更新租户信息
        CoreUtils.copyPropertiesIgnoreNull(tenant, existing);
        tenantRepo.save(existing);

        // 更新管理员信息
        if (isNotEmpty(tenant.getAdminEmail())) {
          User user = userQuery.findByEmail(tenant.getAdminEmail());
          updateTenantAdmin(tenant, user);
        }

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.TENANT,
            existing.getId(),
            existing.getName(),
            OperationMessage.TENANT_UPDATE_DETAILS,
            new Object[]{existing.getName()}
        );

        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Tenant updateStatus(Long id, EnabledStatus status) {
    return new BizTemplate<Tenant>(false) {
      Tenant existing;

      @Override
      protected void checkParams() {
        List<Tenant> tenants = tenantQuery.getSameAccountTenants();

        // 检查租户是否存在
        existing = tenants.stream().filter(x -> x.getId().equals(id)).findFirst()
            .orElse(null);
        if (existing == null) {
          throw ResourceNotFound.of("租户「{0}」不存在", new Object[]{id});
        }

        // 检查如果是主账号不允许修改状态
        if (existing.getAccountType().isMainAccount()) {
          throw ProtocolException.of("不允许修改主账号状态");
        }
      }

      @Override
      protected Tenant process() {
        existing.setStatus(status);
        Tenant saved = tenantRepo.save(existing);

        // 记录操作日志
        if (status.isEnabled()) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.TENANT,
              saved.getId(),
              saved.getName(),
              OperationMessage.TENANT_ENABLE_DETAILS,
              new Object[]{saved.getName()}
          );
        } else {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.TENANT,
              saved.getId(),
              saved.getName(),
              OperationMessage.TENANT_DISABLE_DETAILS,
              new Object[]{saved.getName()}
          );
        }
        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>(false) {
      Tenant existed;

      @Override
      protected void checkParams() {
        List<Tenant> tenants = tenantQuery.getSameAccountTenants();

        // 检查租户是否存在
        existed = tenants.stream().filter(x -> x.getId().equals(id)).findFirst()
            .orElse(null);
        if (existed == null) {
          throw ResourceNotFound.of("租户「{0}」不存在", new Object[]{id});
        }

        // 检查如果是主账号不允许删除
        if (existed.getAccountType().isMainAccount()) {
          throw ProtocolException.of("不允许删除主账号");
        }
      }

      @Override
      protected Void process() {
        // 获取主账号ID（用于配额更新）
        Long mainTenantId = existed.getMainTenantId() != null
            ? existed.getMainTenantId() : existed.getId();
        // 减少租户配额使用量（删除的是子账号）
        quotaManager.decreaseTenantQuota(mainTenantId, QuotaConstant.QuotaTenantCount, 1L);

        // 获取租户名称（在删除前）
        String tenantName = existed.getName();

        // 删除租户资源（应用、菜单、用户、部门、组、授权等等）
        deleteTenantResources(id);

        // 删除租户
        tenantRepo.deleteById(id);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.TENANT,
            id,
            tenantName,
            OperationMessage.TENANT_DELETE_DETAILS,
            new Object[]{tenantName}
        );

        return null;
      }

      /**
       * 使用Repo的deleteByTenantId方法删除租户的所有资源
       */
      private void deleteTenantResources(Long tenantId) {
        // 1. 删除授权角色关系表
        authorizationRoleRepo.deleteByTenantId(tenantId);

        // 2. 删除授权表
        authorizationRepo.deleteByTenantId(tenantId);

        // 3. 删除角色表（排除系统角色和默认角色）
        roleRepo.deleteByTenantId(tenantId);

        // 4. 删除应用菜单表
        applicationMenuRepo.deleteByTenantId(tenantId);

        // 5. 删除应用表 -> 删除了多租户支持
        // applicationRepo.deleteByTenantId(tenantId);

        // 6. 删除组用户关系表
        groupUserRepo.deleteByTenantId(tenantId);

        // 7. 删除组表
        groupCmd.deleteByTenantId(tenantId);

        // 8. 删除部门用户关系表
        departmentUserRepo.deleteByTenantId(tenantId);

        // 9. 删除部门表
        departmentCmd.deleteByTenantId(tenantId);

        // 10. 删除用户
        userCmd.deleteByTenantId(tenantId);

        // 11. 删除配额 -> NOOP 多租户共享主账号配额
        // quotaRepo.deleteByTenantId(tenantId);
      }
    }.execute();
  }

  private void updateTenantAdmin(Tenant tenant, User user) {
    if (isNotEmpty(tenant.getAdminEmail())) {
      user.setEmail(tenant.getAdminEmail());
    }
    if (isNotEmpty(tenant.getAdminName())) {
      user.setName(tenant.getAdminName());
    }
    if (isNotEmpty(tenant.getAdminPhone())) {
      user.setPhone(tenant.getAdminPhone());
    }
    userCmd.update(user);
  }

  @Override
  protected BaseRepository<Tenant, Long> getRepository() {
    return tenantRepo;
  }
}
