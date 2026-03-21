package cloud.xcan.angus.core.gm.application.query.security;

import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.SecurityAuditStatsVo;
import java.time.LocalDate;
import java.util.List;

public interface SecurityQuery {

  /**
   * 获取密码策略
   */
  Security getPasswordPolicy();

  /**
   * 获取登录安全配置
   */
  Security getLoginSecurityConfig();

  /**
   * 获取IP白名单列表
   */
  List<Security> listIpWhitelist();

  /**
   * 获取审计统计
   */
  SecurityAuditStatsVo getAuditStats(LocalDate startDate, LocalDate endDate);

  /**
   * 获取安全通知配置
   */
  Security getNotificationConfig();

  /**
   * 根据密码策略配置校验密码强度
   */
  void validatePasswordByPolicy(String password);
}
