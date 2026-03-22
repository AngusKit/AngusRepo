package cloud.xcan.angus.core.repo.application.cmd.upload.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.repo.application.cmd.upload.UploadCmd;
import cloud.xcan.angus.core.repo.domain.upload.UploadChunk;
import cloud.xcan.angus.core.repo.domain.upload.UploadChunkRepo;
import cloud.xcan.angus.core.repo.domain.upload.UploadStatus;
import cloud.xcan.angus.core.repo.domain.upload.UploadTask;
import cloud.xcan.angus.core.repo.domain.upload.UploadTaskRepo;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class UploadCmdImpl extends CommCmd<UploadTask, Long> implements UploadCmd {

  @Resource
  private UploadTaskRepo uploadTaskRepo;

  @Resource
  private UploadChunkRepo uploadChunkRepo;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UploadTask createTask(UploadTask task) {
    return new BizTemplate<UploadTask>() {
      @Override
      protected void checkParams() {
        if (task.getRepositoryId() == null) {
          throw new RuntimeException("仓库ID不能为空");
        }
        if (task.getFileName() == null || task.getFileName().isBlank()) {
          throw new RuntimeException("文件名不能为空");
        }
        if (task.getFileSize() == null || task.getFileSize() <= 0) {
          throw new RuntimeException("文件大小必须大于0");
        }
      }

      @Override
      protected UploadTask process() {
        task.setUploadToken(UUID.randomUUID().toString());
        task.setStatus(UploadStatus.PENDING);
        task.setExpires(LocalDateTime.now().plusHours(24));
        task.setUploadedChunks(0);
        task.setProgress(0);
        if (task.getEnableChunked() == null) {
          task.setEnableChunked(false);
        }
        if (task.getTotalChunks() == null) {
          task.setTotalChunks(0);
        }
        if (Boolean.TRUE.equals(task.getEnableChunked()) && task.getFileSize() > 0
            && task.getTotalChunks() == 0) {
          // Default to single chunk when chunk count not pre-calculated
          task.setTotalChunks(1);
        }
        insert0(task);
        return task;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void uploadChunk(Long taskId, UploadChunk chunk) {
    new BizTemplate<Void>() {
      UploadTask task;

      @Override
      protected void checkParams() {
        task = uploadTaskRepo.findById(taskId)
            .orElseThrow(() -> new RuntimeException("上传任务不存在: " + taskId));
        if (task.isTerminal()) {
          throw new RuntimeException("上传任务已终止，无法继续上传");
        }
        if (task.isExpired()) {
          throw new RuntimeException("上传任务已过期");
        }
      }

      @Override
      protected Void process() {
        chunk.setUploadTaskId(taskId);
        uploadChunkRepo.save(chunk);

        task.setStatus(UploadStatus.UPLOADING);
        int uploaded = (int) uploadChunkRepo.countByUploadTaskId(taskId);
        task.setUploadedChunks(uploaded);
        task.setProgress(task.getProgressPercent());
        uploadTaskRepo.save(task);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UploadTask completeTask(Long taskId, String description, String license, String tags,
      String metadata) {
    return new BizTemplate<UploadTask>() {
      UploadTask task;

      @Override
      protected void checkParams() {
        task = uploadTaskRepo.findById(taskId)
            .orElseThrow(() -> new RuntimeException("上传任务不存在: " + taskId));
        if (task.isTerminal()) {
          throw new RuntimeException("上传任务已终止");
        }
      }

      @Override
      protected UploadTask process() {
        task.setStatus(UploadStatus.COMPLETED);
        task.setProgress(100);
        uploadTaskRepo.save(task);
        return task;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void cancelTask(Long taskId) {
    new BizTemplate<Void>() {
      UploadTask task;

      @Override
      protected void checkParams() {
        task = uploadTaskRepo.findById(taskId)
            .orElseThrow(() -> new RuntimeException("上传任务不存在: " + taskId));
        if (task.isTerminal()) {
          throw new RuntimeException("上传任务已终止，无法取消");
        }
      }

      @Override
      protected Void process() {
        task.setStatus(UploadStatus.CANCELLED);
        uploadTaskRepo.save(task);
        uploadChunkRepo.deleteByUploadTaskId(taskId);
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<UploadTask> createBatchTasks(List<UploadTask> tasks) {
    return new BizTemplate<List<UploadTask>>() {
      @Override
      protected void checkParams() {
        if (tasks == null || tasks.isEmpty()) {
          throw new RuntimeException("任务列表不能为空");
        }
      }

      @Override
      protected List<UploadTask> process() {
        List<UploadTask> created = new ArrayList<>();
        for (UploadTask task : tasks) {
          task.setUploadToken(UUID.randomUUID().toString());
          task.setStatus(UploadStatus.PENDING);
          task.setExpires(LocalDateTime.now().plusHours(24));
          task.setUploadedChunks(0);
          task.setProgress(0);
          if (task.getEnableChunked() == null) {
            task.setEnableChunked(false);
          }
          if (task.getTotalChunks() == null) {
            task.setTotalChunks(0);
          }
          insert0(task);
          created.add(task);
        }
        return created;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<UploadTask, Long> getRepository() {
    return this.uploadTaskRepo;
  }
}
