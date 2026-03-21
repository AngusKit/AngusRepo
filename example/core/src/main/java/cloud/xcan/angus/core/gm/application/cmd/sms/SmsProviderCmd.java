package cloud.xcan.angus.core.gm.application.cmd.sms;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.sms.SmsProvider;
import java.util.List;

public interface SmsProviderCmd {

  /**
   * 创建服务商配置
   */
  void replace(List<SmsProvider> smsProviders);

  /**
   * 更新服务商配置
   */
  SmsProvider updateProvider(SmsProvider provider);

  /**
   * 更新服务商状态
   */
  SmsProvider updateProviderStatus(Long id, EnabledStatus status);

  /**
   * 设置默认服务商
   */
  SmsProvider setDefaultProvider(Long id);

}
