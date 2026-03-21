package cloud.xcan.angus.core.gm.interfaces.email.facade;

import cloud.xcan.angus.api.gm.email.dto.EmailSendBatchDto;
import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.vo.EmailSendBatchVo;
import cloud.xcan.angus.api.gm.email.vo.EmailSendVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendCustomDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailRecordVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTrackingVo;
import cloud.xcan.angus.remote.PageResult;

public interface EmailFacade {

  /**
   * 发送单封邮件（使用模板）
   */
  EmailSendVo send(EmailSendDto dto);

  /**
   * 批量发送邮件
   */
  EmailSendBatchVo sendBatch(EmailSendBatchDto dto);

  /**
   * 重试发送邮件
   */
  EmailSendVo retry(Long id);

  /**
   * 取消发送邮件
   */
  EmailSendVo cancel(Long id);

  /**
   * 发送自定义邮件（不使用模板）
   */
  EmailSendVo sendCustom(EmailSendCustomDto dto);

  /**
   * 获取邮件统计数据
   */
  EmailStatsVo getStats();

  /**
   * 分页查询邮件记录列表
   */
  PageResult<EmailRecordVo> listRecords(EmailRecordFindDto dto);

  /**
   * 获取邮件打开/点击统计信息
   */
  EmailTrackingVo getEmailTracking(Long id);

}
