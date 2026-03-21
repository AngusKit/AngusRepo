package cloud.xcan.angus.core.gm.application.query.email.impl;

import cloud.xcan.angus.api.enums.ApiType;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.gm.application.query.email.EmailSmtpQuery;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtpRepo;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class EmailSmtpQueryImpl implements EmailSmtpQuery {

  @Resource
  private EmailSmtpRepo emailSmtpRepo;

  @Override
  public Optional<EmailSmtp> findDefault() {
    return new BizTemplate<Optional<EmailSmtp>>(false) {
      @Override
      protected Optional<EmailSmtp> process() {
        // 发送登录邮件时允许读取SMTP配置
        ApiType apiType = PrincipalContext.get().getApiType();
        if (apiType != null && apiType.isUserTypeApi()) {
          PermissionCheck.checkCloudTenantSecurity();
        }
        EmailSmtp defaultSmtp = emailSmtpRepo.findByIsDefaultTrue();
        return Optional.ofNullable(defaultSmtp);
      }
    }.execute();
  }
}

