package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsProviderCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsProviderQuery;
import cloud.xcan.angus.core.gm.domain.sms.SmsProvider;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsProviderFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsProviderUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsProviderAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsProviderVo;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SmsProviderFacadeImpl implements SmsProviderFacade {

  @Resource
  private SmsProviderCmd smsProviderCmd;

  @Resource
  private SmsProviderQuery smsProviderQuery;

  @NameJoin
  @Override
  public SmsProviderVo updateProvider(Long id, SmsProviderUpdateDto dto) {
    SmsProvider provider = SmsProviderAssembler.toUpdateDomain(id, dto);
    SmsProvider saved = smsProviderCmd.updateProvider(provider);
    return SmsProviderAssembler.toProviderVo(saved);
  }

  @NameJoin
  @Override
  public SmsProviderVo updateProviderStatus(Long id, EnabledStatusUpdateDto dto) {
    SmsProvider provider = smsProviderCmd.updateProviderStatus(id, dto.getStatus());
    return SmsProviderAssembler.toProviderVo(provider);
  }

  @NameJoin
  @Override
  public SmsProviderVo setDefaultProvider(Long id) {
    SmsProvider provider = smsProviderCmd.setDefaultProvider(id);
    return SmsProviderAssembler.toProviderVo(provider);
  }

  @NameJoin
  @Override
  public SmsProviderVo getProvider(Long id) {
    SmsProvider provider = smsProviderQuery.findAndCheck(id);
    return SmsProviderAssembler.toProviderVo(provider);
  }

  @NameJoin
  @Override
  public List<SmsProviderVo> listProviders() {
    List<SmsProvider> providers = smsProviderQuery.listProviders();
    return providers.stream()
        .map(SmsProviderAssembler::toProviderVo)
        .collect(Collectors.toList());
  }
}
