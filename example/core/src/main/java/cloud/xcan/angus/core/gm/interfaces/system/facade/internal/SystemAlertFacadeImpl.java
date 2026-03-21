package cloud.xcan.angus.core.gm.interfaces.system.facade.internal;

import static cloud.xcan.angus.api.commonlink.setting.Setting.getDefaultAlertRuleSettings;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.setting.Setting;
import cloud.xcan.angus.api.commonlink.setting.SettingKey;
import cloud.xcan.angus.api.commonlink.setting.alert.AlertRuleSettings;
import cloud.xcan.angus.api.manager.SettingManager;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.setting.SettingCmd;
import cloud.xcan.angus.core.gm.application.query.system.AlertRecordQuery;
import cloud.xcan.angus.core.gm.domain.system.AlertRecord;
import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemAlertFacade;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.AlertRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.AlertRuleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler.AlertRecordAssembler;
import cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler.AlertRuleSettingsAssembler;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AlertRecordVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AlertRuleSettingsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SystemAlertFacadeImpl implements SystemAlertFacade {

  @Resource
  private SettingCmd settingCmd;

  @Resource
  private SettingManager settingManager;

  @Resource
  private AlertRecordQuery alertRecordQuery;

  @NameJoin
  @Override
  public AlertRuleSettingsVo update(List<AlertRuleCreateDto> dto) {
    AlertRuleSettings settings = AlertRuleSettingsAssembler.toUpdateDomain(dto);
    AlertRuleSettings saved = settingCmd.update(settings);
    return AlertRuleSettingsAssembler.toVo(saved);
  }

  @NameJoin
  @Override
  public AlertRuleSettingsVo getSettings() {
    Setting settings = settingManager.getSetting0(SettingKey.ALERT_RULES);
    AlertRuleSettings alertRules = nullSafe(settings != null ? settings.getAlertRules() : null,
        getDefaultAlertRuleSettings());
    return AlertRuleSettingsAssembler.toVo(alertRules);
  }

  @Override
  public PageResult<AlertRecordVo> listAlertRecords(AlertRecordFindDto dto) {
    GenericSpecification<AlertRecord> spec = AlertRecordAssembler.getSpecification(dto);
    Page<AlertRecord> page = alertRecordQuery.list(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, AlertRecordAssembler::toVo);
  }
}
