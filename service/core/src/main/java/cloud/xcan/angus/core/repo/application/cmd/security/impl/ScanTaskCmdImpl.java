package cloud.xcan.angus.core.repo.application.cmd.security.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.security.ScanTaskCmd;
import cloud.xcan.angus.core.repo.domain.security.ScanStatus;
import cloud.xcan.angus.core.repo.domain.security.ScanTask;
import cloud.xcan.angus.core.repo.domain.security.ScanTaskRepo;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class ScanTaskCmdImpl extends CommCmd<ScanTask, String> implements ScanTaskCmd {

  @Autowired(required = false)
  private ScanTaskRepo scanTaskRepo;

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
        existing = scanTaskRepo.findById(scanTask.getId())
            .orElseThrow(() -> new RuntimeException("扫描任务不存在: " + scanTask.getId()));
      }

      @Override
      protected ScanTask process() {
        if (scanTask.getScanType() != null) {
          existing.setScanType(scanTask.getScanType());
        }
        scanTaskRepo.save(existing);
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
        existing = scanTaskRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("扫描任务不存在: " + id));
        if (!existing.isRunning()) {
          throw new RuntimeException("扫描任务已完成，无法取消: " + id);
        }
      }

      @Override
      protected Void process() {
        existing.setStatus(ScanStatus.CANCELLED);
        existing.setEndTime(LocalDateTime.now());
        scanTaskRepo.save(existing);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(String id) {
    scanTaskRepo.deleteById(id);
  }

  @Override
  protected BaseRepository<ScanTask, String> getRepository() {
    return this.scanTaskRepo;
  }
}
