package cloud.xcan.angus.core.repo.interfaces.upload.facade.internal;

import static cloud.xcan.angus.core.repo.interfaces.upload.facade.internal.assembler.UploadAssembler.getSpecification;
import static cloud.xcan.angus.core.repo.interfaces.upload.facade.internal.assembler.UploadAssembler.toBatchCreateEntities;
import static cloud.xcan.angus.core.repo.interfaces.upload.facade.internal.assembler.UploadAssembler.toCreateEntity;
import static cloud.xcan.angus.core.repo.interfaces.upload.facade.internal.assembler.UploadAssembler.toTaskVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.repo.application.cmd.upload.UploadCmd;
import cloud.xcan.angus.core.repo.application.query.upload.UploadQuery;
import cloud.xcan.angus.core.repo.domain.upload.UploadChunk;
import cloud.xcan.angus.core.repo.domain.upload.UploadTask;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.UploadFacade;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.BatchUploadCreateDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadCompleteDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadTaskCreateDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadTaskFindDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.internal.assembler.UploadAssembler;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.vo.UploadStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.vo.UploadTaskVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class UploadFacadeImpl implements UploadFacade {

  @Resource
  private UploadCmd uploadCmd;

  @Resource
  private UploadQuery uploadQuery;

  @Override
  public UploadTaskVo createTask(UploadTaskCreateDto dto) {
    UploadTask entity = toCreateEntity(dto);
    UploadTask created = uploadCmd.createTask(entity);
    return toTaskVo(created);
  }

  @Override
  public void uploadFile(Long taskId, MultipartFile file) {
    UploadChunk chunk = new UploadChunk();
    chunk.setChunkIndex(0);
    chunk.setChunkSize(file.getSize());
    uploadCmd.uploadChunk(taskId, chunk);
  }

  @Override
  public UploadTaskVo completeTask(Long taskId, UploadCompleteDto dto) {
    UploadTask completed = uploadCmd.completeTask(taskId, dto.getDescription(), dto.getLicense(),
        dto.getTags(), dto.getMetadata());
    return toTaskVo(completed);
  }

  @Override
  public void cancelTask(Long taskId) {
    uploadCmd.cancelTask(taskId);
  }

  @Override
  public UploadTaskVo getTask(Long taskId) {
    UploadTask entity = uploadQuery.findAndCheck(taskId);
    return toTaskVo(entity);
  }

  @Override
  public PageResult<UploadTaskVo> listTasks(UploadTaskFindDto dto) {
    return buildVoPageResult(
        uploadQuery.find(getSpecification(dto), dto.tranPage()),
        UploadAssembler::toTaskVo);
  }

  @Override
  public List<UploadTaskVo> createBatchTasks(BatchUploadCreateDto dto) {
    List<UploadTask> entities = toBatchCreateEntities(dto);
    List<UploadTask> created = uploadCmd.createBatchTasks(entities);
    return created.stream().map(UploadAssembler::toTaskVo).collect(Collectors.toList());
  }

  @Override
  public UploadStatisticsVo getStatistics() {
    return uploadQuery.getStatistics();
  }
}
