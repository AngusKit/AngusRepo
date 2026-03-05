package cloud.xcan.angus.core.repo.application.query.upload.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.repo.application.query.upload.UploadQuery;
import cloud.xcan.angus.core.repo.domain.upload.UploadStatus;
import cloud.xcan.angus.core.repo.domain.upload.UploadTask;
import cloud.xcan.angus.core.repo.domain.upload.UploadTaskRepo;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.vo.UploadStatisticsVo;
import jakarta.annotation.Resource;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Biz
public class UploadQueryImpl implements UploadQuery {

  @Resource
  private UploadTaskRepo uploadTaskRepo;

  @Override
  public Page<UploadTask> find(GenericSpecification<UploadTask> spec, PageRequest pageable) {
    return new BizTemplate<Page<UploadTask>>() {
      @Override
      protected Page<UploadTask> process() {
        return uploadTaskRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public Optional<UploadTask> findById(Long id) {
    return uploadTaskRepo.findById(id);
  }

  @Override
  public UploadTask findAndCheck(Long id) {
    return uploadTaskRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("上传任务不存在: " + id));
  }

  @Override
  public UploadStatisticsVo getStatistics() {
    return new BizTemplate<UploadStatisticsVo>() {
      @Override
      protected UploadStatisticsVo process() {
        UploadStatisticsVo stats = new UploadStatisticsVo();
        stats.setTotalTasks(uploadTaskRepo.count());
        stats.setPendingTasks(uploadTaskRepo.countByStatus(UploadStatus.PENDING));
        stats.setCompletedTasks(uploadTaskRepo.countByStatus(UploadStatus.COMPLETED));
        stats.setFailedTasks(uploadTaskRepo.countByStatus(UploadStatus.FAILED));
        return stats;
      }
    }.execute();
  }
}
