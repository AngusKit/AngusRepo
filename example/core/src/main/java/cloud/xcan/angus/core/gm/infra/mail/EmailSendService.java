package cloud.xcan.angus.core.gm.infra.mail;

import cloud.xcan.angus.core.gm.domain.email.Email;

/**
 * 邮件发送服务接口
 */
public interface EmailSendService {

  /**
   * 异步发送邮件
   */
  void sendEmailAsync(Long emailId);

  /**
   * 异步发送邮件
   */
  void sendEmailAsync(Email email);

  /**
   * 同步发送邮件
   */
  void sendEmail(Long emailId);

  /**
   * 同步发送邮件
   */
  void sendEmail(Email email);
}
