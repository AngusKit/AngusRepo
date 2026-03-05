package cloud.xcan.angus.core.repo.application.cmd.upload;

import cloud.xcan.angus.core.repo.domain.upload.UploadChunk;
import cloud.xcan.angus.core.repo.domain.upload.UploadTask;
import java.util.List;

public interface UploadCmd {

  UploadTask createTask(UploadTask task);

  void uploadChunk(Long taskId, UploadChunk chunk);

  UploadTask completeTask(Long taskId, String description, String license, String tags,
      String metadata);

  void cancelTask(Long taskId);

  List<UploadTask> createBatchTasks(List<UploadTask> tasks);
}
