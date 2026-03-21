package cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.system.AlertRecord;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.AlertRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AlertRecordVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

/**
 * 告警记录组装器
 */
public class AlertRecordAssembler {

  public static AlertRecordVo toVo(AlertRecord record) {
    if (record == null) {
      return null;
    }
    AlertRecordVo vo = new AlertRecordVo();
    vo.setId(record.getId());
    vo.setRuleName(record.getRuleName());
    vo.setLevel(record.getLevel());
    vo.setMetric(record.getMetric());
    vo.setCurrentValue(record.getCurrentValue());
    vo.setThreshold(record.getThreshold());
    vo.setMessage(record.getDescription());
    vo.setStatus(record.getStatus());
    vo.setTriggerTime(record.getTriggerTime());
    vo.setInstanceId(record.getInstanceId());
    return vo;
  }

  public static GenericSpecification<AlertRecord> getSpecification(AlertRecordFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "triggerTime", "createdDate", "modifiedDate")
        .orderByFields("id", "triggerTime", "createdDate", "modifiedDate")
        .matchSearchFields("ruleName", "metric", "description")
        .build();
    return new GenericSpecification<>(filters);
  }
}
