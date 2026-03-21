package cloud.xcan.angus.core.gm.application.query.security.impl;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isBlank;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.SecurityRepo;
import cloud.xcan.angus.core.gm.domain.security.SecurityType;
import cloud.xcan.angus.core.gm.domain.security.model.LoginSecurityConfig;
import cloud.xcan.angus.core.gm.domain.security.model.PasswordPolicyConfig;
import cloud.xcan.angus.core.gm.domain.security.model.SecurityNotificationConfig;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityAuditStatsVo;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SecurityQueryImpl implements SecurityQuery {

  @Resource
  private SecurityRepo securityRepo;

  @Override
  public Security getPasswordPolicy() {
    return new BizTemplate<Security>() {
      @Override
      protected Security process() {
        return securityRepo
            .findFirstByType(SecurityType.PASSWORD_POLICY)
            .orElseGet(() -> {
              Security security = new Security();
              security.setName("密码策略");
              security.setType(SecurityType.PASSWORD_POLICY);
              security.setConfig(new PasswordPolicyConfig());
              security.setStatus(EnabledStatus.ENABLED);
              return security;
            });
      }
    }.execute();
  }

  @Override
  public Security getLoginSecurityConfig() {
    return new BizTemplate<Security>() {
      @Override
      protected Security process() {
        return securityRepo
            .findFirstByType(SecurityType.LOGIN_SECURITY)
            .orElseGet(() -> {
              Security security = new Security();
              security.setName("登录安全");
              security.setType(SecurityType.LOGIN_SECURITY);
              security.setConfig(new LoginSecurityConfig());
              security.setStatus(EnabledStatus.ENABLED);
              return security;
            });
      }
    }.execute();
  }

  @Override
  public Security getNotificationConfig() {
    return new BizTemplate<Security>() {
      @Override
      protected Security process() {
        return securityRepo
            .findFirstByType(SecurityType.NOTIFICATION_CONFIG)
            .orElseGet(() -> {
              Security security = new Security();
              security.setName("安全通知");
              security.setType(SecurityType.NOTIFICATION_CONFIG);
              security.setConfig(new SecurityNotificationConfig());
              security.setStatus(EnabledStatus.ENABLED);
              return security;
            });
      }
    }.execute();
  }

  @Override
  public List<Security> listIpWhitelist() {
    return new BizTemplate<List<Security>>() {
      @Override
      protected List<Security> process() {
        return securityRepo.findByType(SecurityType.IP_WHITELIST);
      }
    }.execute();
  }

  @Override
  public SecurityAuditStatsVo getAuditStats(LocalDate startDate, LocalDate endDate) {
    return new BizTemplate<SecurityAuditStatsVo>() {
      @Override
      protected SecurityAuditStatsVo process() {
        SecurityAuditStatsVo vo = new SecurityAuditStatsVo();

        SecurityAuditStatsVo.Period period = new SecurityAuditStatsVo.Period();
        period.setStartDate(startDate);
        period.setEndDate(endDate);
        vo.setPeriod(period);

        // TODO: Implement audit stats calculation
        vo.setTotalEvents(0L);
        vo.setHighRiskEvents(0L);
        vo.setMediumRiskEvents(0L);
        vo.setLowRiskEvents(0L);
        vo.setLoginFailures(0L);
        vo.setPasswordChanges(0L);
        vo.setPermissionChanges(0L);
        vo.setEventsByDay(new ArrayList<>());
        return vo;
      }
    }.execute();
  }

  @Override
  public void validatePasswordByPolicy(String password) {
    new BizTemplate<Void>() {
      PasswordPolicyConfig config;

      @Override
      protected void checkParams() {
        if (isBlank(password)) {
          throw ProtocolException.of("密码不能为空");
        }

        // 获取密码策略配置
        Security security = securityRepo
            .findFirstByType(SecurityType.PASSWORD_POLICY)
            .orElseGet(() -> {
              Security defaultSecurity = new Security();
              defaultSecurity.setName("密码策略");
              defaultSecurity.setType(SecurityType.PASSWORD_POLICY);
              defaultSecurity.setConfig(new PasswordPolicyConfig());
              defaultSecurity.setStatus(EnabledStatus.ENABLED);
              return defaultSecurity;
            });

        if (security.getConfig() instanceof PasswordPolicyConfig) {
          config = (PasswordPolicyConfig) security.getConfig();
        } else {
          // 如果没有配置，使用默认配置
          config = new PasswordPolicyConfig();
        }
      }

      @Override
      protected Void process() {
        int passwordLength = password.length();

        // 检查密码长度
        Integer minLength = nullSafe(config.getMinLength(), 6);
        Integer maxLength = nullSafe(config.getMaxLength(), 20);
        if (passwordLength < minLength) {
          throw ProtocolException.of("密码长度不能小于{0}个字符", new Object[]{minLength});
        }
        if (passwordLength > maxLength) {
          throw ProtocolException.of("密码长度不能大于{0}个字符", new Object[]{maxLength});
        }

        // 检查是否包含大写字母
        if (Boolean.TRUE.equals(config.getRequireUppercase())) {
          if (!password.matches(".*[A-Z].*")) {
            throw ProtocolException.of("密码必须包含至少一个大写字母");
          }
        }

        // 检查是否包含小写字母
        if (Boolean.TRUE.equals(config.getRequireLowercase())) {
          if (!password.matches(".*[a-z].*")) {
            throw ProtocolException.of("密码必须包含至少一个小写字母");
          }
        }

        // 检查是否包含数字
        if (Boolean.TRUE.equals(config.getRequireNumbers())) {
          if (!password.matches(".*\\d.*")) {
            throw ProtocolException.of("密码必须包含至少一个数字");
          }
        }

        // 检查是否包含特殊字符
        if (Boolean.TRUE.equals(config.getRequireSpecialChars())) {
          if (!password.matches(".*[@$!%*?&#_\\-+=().,;:<>\\[\\]{}|~`\"'/\\\\].*")) {
            throw ProtocolException.of("密码必须包含至少一个特殊字符");
          }
        }

        return null;
      }
    }.execute();
  }
}
