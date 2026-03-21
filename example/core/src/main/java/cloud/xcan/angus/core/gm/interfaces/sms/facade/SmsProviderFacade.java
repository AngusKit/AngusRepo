package cloud.xcan.angus.core.gm.interfaces.sms.facade;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsProviderUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsProviderVo;
import java.util.List;

public interface SmsProviderFacade {

  /**
   * 更新服务商配置
   */
  SmsProviderVo updateProvider(Long id, SmsProviderUpdateDto dto);

  /**
   * 更新服务商状态
   */
  SmsProviderVo updateProviderStatus(Long id, EnabledStatusUpdateDto dto);

  /**
   * 设置默认服务商
   */
  SmsProviderVo setDefaultProvider(Long id);

  /**
   * 获取服务商详情
   */
  SmsProviderVo getProvider(Long id);

  /**
   * 获取短信服务商列表
   */
  List<SmsProviderVo> listProviders();

}
