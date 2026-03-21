package cloud.xcan.angus.core.gm.application.query.quota;

import cloud.xcan.angus.api.commonlink.quota.Quota;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaStatisticsVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaUsageVo;
import java.util.List;

/**
 * 资源配额查询服务接口
 */
public interface QuotaQuery {

  /**
   * 根据编码查询并检查存在性
   */
  Quota findByCodeAndCheck(String code);

  /**
   * 分页查询资源配额列表
   */
  List<Quota> list(String appCode, Boolean enabled);

  /**
   * 查询配额统计信息
   */
  QuotaStatisticsVo getStatistics();

  /**
   * 查询单个资源的使用情况
   */
  QuotaUsageVo getUsage(String code);

  /**
   * 查询租户配额
   */
  List<Quota> findByMainTenantId(Long id);
}
