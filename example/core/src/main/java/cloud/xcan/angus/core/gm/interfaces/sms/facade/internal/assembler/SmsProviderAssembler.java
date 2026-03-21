package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.core.gm.domain.sms.SmsProvider;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsProviderUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsProviderVo;

public class SmsProviderAssembler {

  public static SmsProvider toUpdateDomain(Long id, SmsProviderUpdateDto dto) {
    SmsProvider provider = new SmsProvider();
    provider.setId(id);
    provider.setName(dto.getName());
    provider.setConfig(dto.getConfig());
    provider.setIsDefault(nullSafe(dto.getIsDefault(), false));
    return provider;
  }

  public static SmsProviderVo toProviderVo(SmsProvider provider) {
    SmsProviderVo vo = new SmsProviderVo();
    vo.setId(provider.getId());
    vo.setName(provider.getName());
    vo.setIsDefault(nullSafe(provider.getIsDefault(), false));
    vo.setStatus(provider.getStatus());
    vo.setConfig(provider.getConfig());

    // 设置审计字段
    vo.setCreatedBy(provider.getCreatedBy());
    vo.setCreatedDate(provider.getCreatedDate());
    vo.setModifiedBy(provider.getModifiedBy());
    vo.setModifiedDate(provider.getModifiedDate());
    return vo;
  }

}
