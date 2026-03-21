package cloud.xcan.angus.core.gm.interfaces.quota.facade.internal;

import cloud.xcan.angus.api.commonlink.quota.Quota;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.quota.QuotaCmd;
import cloud.xcan.angus.core.gm.application.query.quota.QuotaQuery;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.QuotaFacade;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.BatchUpdateQuotaLimitsDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.QuotaFindDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.UpdateQuotaDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.UpdateQuotaStatusDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.internal.assembler.QuotaAssembler;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaStatisticsVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaUsageVo;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaVo;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class QuotaFacadeImpl implements QuotaFacade {

  @Resource
  private QuotaCmd quotaCmd;

  @Resource
  private QuotaQuery quotaQuery;

  @NameJoin
  @Override
  public QuotaVo update(String code, UpdateQuotaDto dto) {
    Quota quota = QuotaAssembler.toUpdateDomain(dto);
    Quota saved = quotaCmd.update(quota);
    return QuotaAssembler.toVo(saved);
  }

  @NameJoin
  @Override
  public List<QuotaVo> batchUpdateLimits(BatchUpdateQuotaLimitsDto dto) {
    List<String> codes = dto.getQuotas().stream()
        .map(BatchUpdateQuotaLimitsDto.QuotaLimitDto::getCode)
        .collect(Collectors.toList());
    List<Long> limits = dto.getQuotas().stream()
        .map(BatchUpdateQuotaLimitsDto.QuotaLimitDto::getLimit)
        .collect(Collectors.toList());
    List<Quota> quotas = quotaCmd.batchUpdateLimits(codes, limits);
    return QuotaAssembler.toVoList(quotas);
  }

  @NameJoin
  @Override
  public QuotaVo updateStatus(String code, UpdateQuotaStatusDto dto) {
    Quota saved = quotaCmd.updateStatus(code, dto.getEnabled());
    return QuotaAssembler.toVo(saved);
  }

  @NameJoin
  @Override
  public QuotaVo getByCode(String code) {
    Quota quota = quotaQuery.findByCodeAndCheck(code);
    return QuotaAssembler.toVo(quota);
  }

  @NameJoin
  @Override
  public List<QuotaVo> list(QuotaFindDto dto) {
    List<Quota> list = quotaQuery.list(dto.getAppCode(), dto.getEnabled());
    return QuotaAssembler.toVoList(list);
  }

  @Override
  public QuotaStatisticsVo getStatistics() {
    return quotaQuery.getStatistics();
  }

  @Override
  public QuotaUsageVo getUsage(String code) {
    return quotaQuery.getUsage(code);
  }
}
