package cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.email.EmailSmtp;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpTestDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpVo;

/**
 * SMTP配置数据转换器
 */
public class EmailSmtpAssembler {

  public static EmailSmtp toUpdateDomain(EmailSmtpUpdateDto dto) {
    EmailSmtp smtp = new EmailSmtp();
    smtp.setHost(dto.getHost());
    smtp.setPort(dto.getPort());
    smtp.setUsername(dto.getUsername());
    if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
      smtp.setPassword(dto.getPassword());
    }
    smtp.setFromName(dto.getFromName());
    smtp.setFromEmail(dto.getFromEmail());
    smtp.setUseSsl(dto.getUseSsl() != null ? dto.getUseSsl() : true);
    smtp.setUseStartTls(dto.getUseStartTls() != null ? dto.getUseStartTls() : false);
    smtp.setIsDefault(true);
    return smtp;
  }

  public static EmailSmtp toEmailTestSmtp(EmailSmtpTestDto dto) {
    EmailSmtp smtp = new EmailSmtp();
    smtp.setHost(dto.getHost());
    smtp.setPort(dto.getPort());
    smtp.setUsername(dto.getUsername());
    smtp.setPassword(dto.getPassword());
    smtp.setUseSsl(dto.getUseSsl() != null ? dto.getUseSsl() : true);
    smtp.setUseStartTls(dto.getUseStartTls() != null ? dto.getUseStartTls() : false);
    return smtp;
  }

  public static EmailSmtpVo toVo(EmailSmtp smtp) {
    EmailSmtpVo vo = new EmailSmtpVo();
    vo.setId(smtp.getId());
    vo.setHost(smtp.getHost());
    vo.setPort(smtp.getPort());
    vo.setUsername(smtp.getUsername());
    vo.setPassword(smtp.getPassword() != null ? "******" : null); // Mask password
    vo.setFromName(smtp.getFromName());
    vo.setFromEmail(smtp.getFromEmail());
    vo.setUseSsl(smtp.getUseSsl());
    vo.setUseStartTls(smtp.getUseStartTls());
    vo.setIsDefault(smtp.getIsDefault());

    // 设置审计字段
    vo.setCreatedBy(smtp.getCreatedBy());
    vo.setCreatedDate(smtp.getCreatedDate());
    vo.setModifiedBy(smtp.getModifiedBy());
    vo.setModifiedDate(smtp.getModifiedDate());
    return vo;
  }
}
