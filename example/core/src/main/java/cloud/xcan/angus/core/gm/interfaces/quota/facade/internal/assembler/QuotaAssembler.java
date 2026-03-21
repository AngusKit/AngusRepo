package cloud.xcan.angus.core.gm.interfaces.quota.facade.internal.assembler;

import cloud.xcan.angus.api.commonlink.quota.Quota;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.dto.UpdateQuotaDto;
import cloud.xcan.angus.core.gm.interfaces.quota.facade.vo.QuotaVo;
import java.util.List;
import java.util.stream.Collectors;

public class QuotaAssembler {

  public static Quota toUpdateDomain(UpdateQuotaDto dto) {
    Quota quota = new Quota();
    quota.setCode(dto.getCode());
    quota.setLimit(dto.getLimit());
    quota.setUnit(dto.getUnit());
    quota.setDescription(dto.getDescription());
    quota.setIcon(dto.getIcon());
    return quota;
  }

  public static QuotaVo toVo(Quota quota) {
    if (quota == null) {
      return null;
    }
    QuotaVo vo = new QuotaVo();
    vo.setCode(quota.getCode());
    vo.setName(quota.getName());
    vo.setAppCode(quota.getAppCode());
    vo.setLimit(quota.getLimit());
    vo.setUsed(quota.getUsed());
    vo.setUnit(quota.getUnit());
    vo.setDescription(quota.getDescription());
    vo.setIcon(quota.getIcon());
    vo.setEnabled(quota.getEnabled());

    // 计算使用率
    if (quota.getLimit() != null && quota.getLimit() > 0) {
      vo.setUsagePercentage((double) quota.getUsed() / quota.getLimit() * 100);
    } else {
      vo.setUsagePercentage(0.0);
    }
    vo.setIsLicenseControl(quota.getIsLicenseControl());

    // 设置审计信息
    vo.setTenantId(quota.getTenantId());
    vo.setModifiedBy(quota.getModifiedBy());
    vo.setModifiedDate(quota.getModifiedDate());
    return vo;
  }

  public static List<QuotaVo> toVoList(List<Quota> quotas) {
    return quotas.stream().map(QuotaAssembler::toVo).collect(Collectors.toList());
  }
}
