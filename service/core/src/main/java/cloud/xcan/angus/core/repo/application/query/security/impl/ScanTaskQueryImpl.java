package cloud.xcan.angus.core.repo.application.query.security.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.security.ScanTaskQuery;
import cloud.xcan.angus.core.repo.domain.security.ScanStatus;
import cloud.xcan.angus.core.repo.domain.security.ScanTask;
import cloud.xcan.angus.core.repo.domain.security.ScanTaskListRepo;
import cloud.xcan.angus.core.repo.domain.security.ScanTaskRepo;
import cloud.xcan.angus.core.repo.domain.security.ScanTaskSearchRepo;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanStatisticsVo;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class ScanTaskQueryImpl implements ScanTaskQuery {

  @Resource
  private ScanTaskRepo scanTaskRepo;

  @Resource
  private ScanTaskListRepo scanTaskListRepo;

  @Resource
  private ScanTaskSearchRepo scanTaskSearchRepo;

  @Override
  public Page<ScanTask> find(GenericSpecification<ScanTask> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<ScanTask>>() {
      @Override
      protected Page<ScanTask> process() {
        return fullTextSearch
            ? scanTaskSearchRepo.find(spec.getCriteria(), pageable, ScanTask.class, match)
            : scanTaskListRepo.find(spec.getCriteria(), pageable, ScanTask.class, null);
      }
    }.execute();
  }

  @Override
  public Optional<ScanTask> findById(String id) {
    return scanTaskRepo.findById(id);
  }

  @Override
  public ScanTask findAndCheck(String id) {
    return scanTaskRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("扫描任务不存在: " + id));
  }

  @Override
  public ScanStatisticsVo getStatistics() {
    return new BizTemplate<ScanStatisticsVo>() {
      @Override
      protected ScanStatisticsVo process() {
        String tenantId = PrincipalContext.getTenantId();
        ScanStatisticsVo stats = new ScanStatisticsVo();
        stats.setTotalScans(scanTaskRepo.countTotalScans(tenantId));
        stats.setCompletedScans(scanTaskRepo.countByStatus(tenantId, ScanStatus.COMPLETED));
        stats.setFailedScans(scanTaskRepo.countByStatus(tenantId, ScanStatus.FAILED));
        stats.setRunningScans(scanTaskRepo.countByStatus(tenantId, ScanStatus.SCANNING));
        stats.setPendingScans(scanTaskRepo.countByStatus(tenantId, ScanStatus.PENDING));
        stats.setTotalVulnerabilities(scanTaskRepo.sumVulnerabilities(tenantId));
        return stats;
      }
    }.execute();
  }
}
