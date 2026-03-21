package cloud.xcan.angus.core.gm.application.cmd.system.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.system.AlertRecordCmd;
import cloud.xcan.angus.core.gm.application.query.system.AlertRecordQuery;
import cloud.xcan.angus.core.gm.domain.system.AlertRecord;
import cloud.xcan.angus.core.gm.domain.system.AlertRecordRepo;
import cloud.xcan.angus.core.gm.domain.system.enums.AlertRecordStatus;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 告警记录命令服务实现
 */
@Service
public class AlertRecordCmdImpl extends CommCmd<AlertRecord, Long> implements AlertRecordCmd {

  @Resource
  private AlertRecordRepo alertRecordRepo;

  @Resource
  private AlertRecordQuery alertRecordQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AlertRecord create(AlertRecord alertRecord) {
    return new BizTemplate<AlertRecord>() {
      @Override
      protected AlertRecord process() {
        // 设置默认值
        if (alertRecord.getStatus() == null) {
          alertRecord.setStatus(AlertRecordStatus.ACTIVE);
        }
        if (alertRecord.getTriggerTime() == null) {
          alertRecord.setTriggerTime(LocalDateTime.now());
        }
        insert(alertRecord);
        return alertRecord;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AlertRecord update(Long id, AlertRecord alertRecord) {
    return new BizTemplate<AlertRecord>() {
      AlertRecord existing;

      @Override
      protected void checkParams() {
        existing = alertRecordQuery.findAndCheck(id);
      }

      @Override
      protected AlertRecord process() {
        // 更新字段
        if (alertRecord.getRuleName() != null) {
          existing.setRuleName(alertRecord.getRuleName());
        }
        if (alertRecord.getMetric() != null) {
          existing.setMetric(alertRecord.getMetric());
        }
        if (alertRecord.getMetricName() != null) {
          existing.setMetricName(alertRecord.getMetricName());
        }
        if (alertRecord.getCurrentValue() != null) {
          existing.setCurrentValue(alertRecord.getCurrentValue());
        }
        if (alertRecord.getThreshold() != null) {
          existing.setThreshold(alertRecord.getThreshold());
        }
        if (alertRecord.getCondition() != null) {
          existing.setCondition(alertRecord.getCondition());
        }
        if (alertRecord.getLevel() != null) {
          existing.setLevel(alertRecord.getLevel());
        }
        if (alertRecord.getStatus() != null) {
          existing.setStatus(alertRecord.getStatus());
        }
        if (alertRecord.getDescription() != null) {
          existing.setDescription(alertRecord.getDescription());
        }
        if (alertRecord.getComponentName() != null) {
          existing.setComponentName(alertRecord.getComponentName());
        }
        if (alertRecord.getComponentStatus() != null) {
          existing.setComponentStatus(alertRecord.getComponentStatus());
        }
        alertRecordRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AlertRecord resolve(Long id) {
    return new BizTemplate<AlertRecord>() {
      AlertRecord existing;

      @Override
      protected void checkParams() {
        existing = alertRecordQuery.findAndCheck(id);
      }

      @Override
      protected AlertRecord process() {
        existing.setStatus(AlertRecordStatus.RESOLVED);
        existing.setResolvedTime(LocalDateTime.now());
        alertRecordRepo.save(existing);
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void resolveBatch(List<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        List<AlertRecord> records = alertRecordRepo.findAllById(ids);
        LocalDateTime now = LocalDateTime.now();
        for (AlertRecord record : records) {
          record.setStatus(AlertRecordStatus.RESOLVED);
          record.setResolvedTime(now);
        }
        alertRecordRepo.saveAll(records);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(List<Long> ids) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        alertRecordRepo.deleteAllById(ids);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<AlertRecord, Long> getRepository() {
    return this.alertRecordRepo;
  }
}
