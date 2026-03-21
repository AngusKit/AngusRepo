package cloud.xcan.angus.core.gm.application.query.tenant;

import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.tenant.enums.AccountType;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantUsageVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface TenantQuery {

  /**
   * 查找并校验租户存在性（包含主账号和子账号）
   */
  Tenant findAndCheck(Long id);

  /**
   * 分页查询租户
   */
  Page<Tenant> find(GenericSpecification<Tenant> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 获取租户统计数据
   */
  TenantStatsVo getStats();

  /**
   * 获取租户使用情况
   */
  TenantUsageVo getUsage(Long id);

  /**
   * 根据账号类型查询租户列表
   */
  List<Tenant> findByAccountType(AccountType accountType);

  /**
   * 查询租户列表，返回租户主账号和所有子账号
   */
  List<Tenant> getSameAccountTenants();

  /**
   * 查询租户Ids列表，返回租户主账号和所有子账号
   */
  List<Long> getTenantIdsBySameAccount();

  /**
   * 查询租户主账号
   */
  Tenant getMainTenantOfSameAccount(Long tenantId);

  /**
   * 查询指定租户同账号租户列表，返回租户主账号和所有子账号
   */
  List<Tenant> getSameAccountTenants(Long tenantId);

  /**
   * 查询指定租户同账号租户ID列表，返回租户主账号和所有子账号
   */
  List<Long> getTenantIdsBySameAccount(Long tenantId);

  /**
   * 根据当前租户ID查询主租户账号ID
   */
  Long getMainTenantId(Long currentTenantId);

  /**
   * 检查指定租户ID是否为主租户
   */
  boolean isMainTenant(Long tenantId);

  /**
   * 检查多租户权限
   */
  void checkMultitenancyPermission();

  /**
   * 检查租户状态是否有效
   */
  void checkValidStatus(String tenantId);

}
