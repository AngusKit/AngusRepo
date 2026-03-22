package cloud.xcan.angus.core.repo.application.cmd.security.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.security.ScanTaskCmd;
import cloud.xcan.angus.core.repo.application.query.security.ScanTaskQuery;
import cloud.xcan.angus.core.repo.domain.security.ScanStatus;
import cloud.xcan.angus.core.repo.domain.security.ScanTask;
import cloud.xcan.angus.core.repo.domain.security.ScanTaskRepo;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Biz
public class ScanTaskCmdImpl extends CommCmd<ScanTask, String> implements ScanTaskCmd {

  @Resource
  private ScanTaskRepo scanTaskRepo;

  @Resource
  private ScanTaskQuery scanTaskQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ScanTask create(ScanTask scanTask) {
    return new BizTemplate<ScanTask>() {
      @Override
      protected ScanTask process() {
        scanTask.setId(UUID.randomUUID().toString());
        scanTask.setStatus(ScanStatus.PENDING);
        scanTask.setProgress(0);
        scanTask.setVulnerabilityCount(0);
        scanTask.setCriticalCount(0);
        scanTask.setHighCount(0);
        scanTask.setMediumCount(0);
        scanTask.setLowCount(0);
        scanTask.setCreatedDate(LocalDateTime.now());
        insert0(scanTask);
        log.info("Scan task created: id={}", scanTask.getId());
        return scanTask;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public ScanTask update(ScanTask scanTask) {
    return new BizTemplate<ScanTask>() {
      ScanTask existing;

      @Override
      protected void checkParams() {
        existing = scanTaskQuery.findAndCheck(scanTask.getId());
      }

      @Override
      protected ScanTask process() {
        if (scanTask.getScanType() != null) {
          existing.setScanType(scanTask.getScanType());
        }
        scanTaskRepo.save(existing);
        log.info("Scan task updated: id={}", scanTask.getId());
        return existing;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void cancel(String id) {
    new BizTemplate<Void>() {
      ScanTask existing;

      @Override
      protected void checkParams() {
        existing = scanTaskQuery.findAndCheck(id);
        if (!existing.isRunning()) {
          throw ProtocolException.of("扫描任务已完成，无法取消");
        }
      }

      @Override
      protected Void process() {
        existing.setStatus(ScanStatus.CANCELLED);
        existing.setEndTime(LocalDateTime.now());
        scanTaskRepo.save(existing);
        log.info("Scan task cancelled: id={}", id);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        log.warn("Scan task deleted: id={}", id);
        scanTaskRepo.deleteById(id);
        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<ScanTask, String> getRepository() {
    return this.scanTaskRepo;
  }
}
