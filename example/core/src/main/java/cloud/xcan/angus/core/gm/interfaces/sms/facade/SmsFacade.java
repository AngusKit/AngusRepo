package cloud.xcan.angus.core.gm.interfaces.sms.facade;

import cloud.xcan.angus.api.gm.sms.dto.SmsSendBatchDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendBatchVo;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTestDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsRecordVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsStatsVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTestVo;
import cloud.xcan.angus.remote.PageResult;

public interface SmsFacade {

  // ==================== 短信发送 ====================

  /**
   * <p>发送短信</p>
   */
  SmsSendVo send(SmsSendDto dto);

  /**
   * <p>批量发送短信</p>
   */
  SmsSendBatchVo sendBatch(SmsSendBatchDto dto);

  /**
   * <p>测试短信发送</p>
   */
  SmsTestVo test(SmsTestDto dto);

  // ==================== 查询 ====================

  /**
   * <p>获取短信统计数据</p>
   */
  SmsStatsVo getStats();

  /**
   * <p>获取短信记录列表</p>
   */
  PageResult<SmsRecordVo> listRecords(SmsRecordFindDto dto);
}
