package cloud.xcan.angus.core.gm.interfaces.quota.facade;

import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.BatchUpdateQuotaLimitsDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.QuotaFindDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.UpdateQuotaDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.UpdateQuotaStatusDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaStatisticsVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaUsageVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaVo;
import java.util.List;

/**
 * 资源配额Facade接口
 */
public interface QuotaFacade {

  /**
   * 更新资源配额
   */
  QuotaVo update(String code, UpdateQuotaDto dto);

  /**
   * 批量更新配额限额
   */
  List<QuotaVo> batchUpdateLimits(BatchUpdateQuotaLimitsDto dto);

  /**
   * 修改配额状态
   */
  QuotaVo updateStatus(String code, UpdateQuotaStatusDto dto);

  /**
   * 查询资源配额详情
   */
  QuotaVo getByCode(String code);

  /**
   * 查询资源配额列表
   */
  List<QuotaVo> list(QuotaFindDto dto);

  /**
   * 查询配额统计信息
   */
  QuotaStatisticsVo getStatistics();

  /**
   * 查询单个资源的使用情况
   */
  QuotaUsageVo getUsage(String code);
}
