package cloud.xcan.angus.core.gm.interfaces.tenant.facade;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantFindDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.dto.TenantUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatsVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantStatusUpdateVo;
import cloud.xcan.angus.core.gm.interfaces.tenant.facade.vo.TenantUsageVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface TenantFacade {

  /**
   * 创建租户
   */
  TenantDetailVo create(TenantCreateDto dto);

  /**
   * 更新租户
   */
  TenantDetailVo update(Long id, TenantUpdateDto dto);

  /**
   * 更新租户状态
   */
  TenantStatusUpdateVo updateStatus(Long id, EnabledStatusUpdateDto dto);

  /**
   * 删除租户
   */
  void delete(Long id);

  /**
   * 获取租户详情
   */
  TenantDetailVo getDetail(Long id);

  /**
   * 获取租户列表（分页）
   */
  PageResult<TenantDetailVo> list(TenantFindDto dto);

  /**
   * 查询当前租户同账号所有租户信息
   *
   * @return 同账号租户详情列表（包含主账号及所有子账号）
   */
  List<TenantDetailVo> getSameAccountTenants();

  /**
   * 获取租户统计数据
   */
  TenantStatsVo getStats();

  /**
   * 获取租户使用统计
   */
  TenantUsageVo getUsage(Long id);

}
