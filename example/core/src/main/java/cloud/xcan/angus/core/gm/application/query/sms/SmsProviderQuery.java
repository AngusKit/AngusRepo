package cloud.xcan.angus.core.gm.application.query.sms;

import cloud.xcan.angus.core.gm.domain.sms.SmsProvider;
import java.util.List;

public interface SmsProviderQuery {

  /**
   * 获取所有短信服务商列表
   */
  List<SmsProvider> listProviders();

  /**
   * 根据ID查找服务商并检查是否存在
   */
  SmsProvider findAndCheck(Long id);

  /**
   * 查找默认服务商
   */
  SmsProvider findDefaultProvider();
}
