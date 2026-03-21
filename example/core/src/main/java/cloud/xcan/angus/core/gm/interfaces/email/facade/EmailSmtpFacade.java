package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpTestDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpTestVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpVo;

public interface EmailSmtpFacade {

  /**
   * 更新SMTP配置
   */
  EmailSmtpVo updateSmtpConfig(EmailSmtpUpdateDto dto);

  /**
   * 获取SMTP配置
   */
  EmailSmtpVo getSmtpConfig();

  /**
   * 测试SMTP连接
   */
  EmailSmtpTestVo testSmtpConnection(EmailSmtpTestDto dto);
}
