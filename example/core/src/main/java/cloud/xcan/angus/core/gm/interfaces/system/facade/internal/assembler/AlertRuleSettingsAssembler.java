package cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.setting.alert.AlertRule;
import cloud.xcan.angus.api.commonlink.setting.alert.AlertRuleSettings;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.AlertRuleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AlertRuleSettingsVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AlertRuleVo;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AlertRuleSettingsAssembler {

  public static AlertRuleSettings toUpdateDomain(List<AlertRuleCreateDto> dto) {
    AlertRuleSettings settings = new AlertRuleSettings();
    List<AlertRule> rules = dto.stream()
        .map(AlertRuleSettingsAssembler::toAlertRule)
        .collect(Collectors.toList());
    settings.setRules(rules);
    return settings;
  }

  public static AlertRuleSettingsVo toVo(AlertRuleSettings settings) {
    AlertRuleSettingsVo vo = new AlertRuleSettingsVo();
    if (!isEmpty(settings.getRules())) {
      List<AlertRuleVo> ruleVos = settings.getRules().stream()
          .map(AlertRuleSettingsAssembler::toAlertRuleVo)
          .collect(Collectors.toList());
      vo.setRules(ruleVos);
    } else {
      vo.setRules(new ArrayList<>());
    }
    return vo;
  }

  private static AlertRule toAlertRule(AlertRuleCreateDto dto) {
    AlertRule rule = new AlertRule();
    rule.setName(dto.getName());
    rule.setMetric(dto.getMetric());
    rule.setCondition(dto.getCondition());
    rule.setThreshold(dto.getThreshold());
    rule.setDuration(dto.getDuration());
    rule.setLevel(dto.getLevel());
    return rule;
  }

  private static AlertRuleVo toAlertRuleVo(AlertRule rule) {
    AlertRuleVo vo = new AlertRuleVo();
    vo.setName(rule.getName());
    vo.setMetric(rule.getMetric());
    vo.setCondition(rule.getCondition());
    vo.setThreshold(rule.getThreshold());
    vo.setDuration(rule.getDuration());
    vo.setLevel(rule.getLevel() != null ? rule.getLevel().name() : null);
    vo.setTriggerCount(0L); // 触发次数默认为0
    return vo;
  }
}
