package cloud.xcan.angus.core.gm.interfaces.email.facade.internal;

import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailSmtpAssembler.toEmailTestSmtp;
import static java.util.Objects.isNull;

import cloud.xcan.angus.core.gm.application.cmd.email.EmailSmtpCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailSmtpQuery;
import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import cloud.xcan.angus.core.gm.infra.mail.EmailSender;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailSmtpFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpTestDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailSmtpAssembler;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpTestVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpVo;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailSmtpFacadeImpl implements EmailSmtpFacade {

  @Resource
  private EmailSmtpCmd emailSmtpCmd;

  @Resource
  private EmailSmtpQuery emailSmtpQuery;

  @Resource
  private EmailSender emailSender;

  @Override
  public EmailSmtpVo updateSmtpConfig(EmailSmtpUpdateDto dto) {
    EmailSmtp smtp = EmailSmtpAssembler.toUpdateDomain(dto);
    EmailSmtp saved = emailSmtpCmd.save(smtp);
    return EmailSmtpAssembler.toVo(saved);
  }

  @Override
  public EmailSmtpVo getSmtpConfig() {
    EmailSmtp smtp = emailSmtpQuery.findDefault().orElse(null);
    return isNull(smtp) ? new EmailSmtpVo() : EmailSmtpAssembler.toVo(smtp);
  }

  @Override
  public EmailSmtpTestVo testSmtpConnection(EmailSmtpTestDto dto) {
    EmailSmtpTestVo vo = new EmailSmtpTestVo();
    vo.setTestTime(LocalDateTime.now());

    try {
      EmailSmtp smtp = toEmailTestSmtp(dto);
      boolean connected = emailSender.testConnection(smtp);
      vo.setConnected(connected);
      vo.setMessage(connected ? "SMTP连接测试成功" : "SMTP连接测试失败");
    } catch (MessagingException e) {
      log.error("SMTP连接测试失败", e);
      vo.setConnected(false);
      vo.setMessage("SMTP连接失败: " + (e.getMessage() != null ? e.getMessage()
          : e.getClass().getSimpleName()));
    } catch (Exception e) {
      log.error("SMTP连接测试异常", e);
      vo.setConnected(false);
      vo.setMessage("SMTP连接测试异常: " + (e.getMessage() != null ? e.getMessage()
          : e.getClass().getSimpleName()));
    }
    return vo;
  }

}
