package cloud.xcan.angus.core.repo.interfaces.upload.facade;

import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.BatchUploadCreateDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadCompleteDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadTaskCreateDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadTaskFindDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.vo.UploadStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.vo.UploadTaskVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface UploadFacade {

  UploadTaskVo createTask(UploadTaskCreateDto dto);

  void uploadFile(Long taskId, MultipartFile file);

  UploadTaskVo completeTask(Long taskId, UploadCompleteDto dto);

  void cancelTask(Long taskId);

  UploadTaskVo getTask(Long taskId);

  PageResult<UploadTaskVo> listTasks(UploadTaskFindDto dto);

  List<UploadTaskVo> createBatchTasks(BatchUploadCreateDto dto);

  UploadStatisticsVo getStatistics();
}
