package cloud.xcan.angus.core.gm.interfaces.system.facade;

import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.AlertRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.AlertRuleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AlertRecordVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AlertRuleSettingsVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface SystemAlertFacade {

  /**
   * 更新告警规则设置
   */
  AlertRuleSettingsVo update(List<AlertRuleCreateDto> dto);

  /**
   * 查询告警规则设置
   */
  AlertRuleSettingsVo getSettings();

  /**
   * 分页查询告警记录
   */
  PageResult<AlertRecordVo> listAlertRecords(AlertRecordFindDto dto);
}
