package cloud.xcan.angus.core.gm.application.query.sms.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.sms.SmsProviderQuery;
import cloud.xcan.angus.core.gm.domain.sms.SmsProvider;
import cloud.xcan.angus.core.gm.domain.sms.SmsProviderRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SmsProviderQueryImpl implements SmsProviderQuery {

  @Resource
  private SmsProviderRepo smsProviderRepo;

  @Override
  public List<SmsProvider> listProviders() {
    return new BizTemplate<List<SmsProvider>>() {
      @Override
      protected List<SmsProvider> process() {
        return smsProviderRepo.findAll();
      }
    }.execute();
  }

  @Override
  public SmsProvider findAndCheck(Long id) {
    return new BizTemplate<SmsProvider>() {
      @Override
      protected SmsProvider process() {
        return smsProviderRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("短信服务商「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public SmsProvider findDefaultProvider() {
    return new BizTemplate<SmsProvider>() {
      @Override
      protected SmsProvider process() {
        return smsProviderRepo.findByIsDefaultTrue()
            .orElseThrow(() -> ResourceNotFound.of("未找到默认短信服务商", new Object[]{}));
      }
    }.execute();
  }
}
