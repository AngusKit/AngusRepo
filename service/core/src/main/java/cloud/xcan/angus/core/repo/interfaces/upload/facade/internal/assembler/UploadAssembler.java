package cloud.xcan.angus.core.repo.interfaces.upload.facade.internal.assembler;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.repo.domain.upload.UploadTask;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.BatchUploadCreateDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadFileInfoDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadTaskCreateDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.dto.UploadTaskFindDto;
import cloud.xcan.angus.core.repo.interfaces.upload.facade.vo.UploadTaskVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UploadAssembler {

  public static UploadTask toCreateEntity(UploadTaskCreateDto dto) {
    UploadTask entity = new UploadTask();
    entity.setRepositoryId(dto.getRepositoryId());
    entity.setFileName(dto.getFileName());
    entity.setFileSize(dto.getFileSize());
    entity.setChecksum(dto.getChecksum());
    entity.setPath(dto.getPath());
    entity.setVersion(dto.getVersion());
    entity.setEnableChunked(dto.getEnableChunked());
    if (Boolean.TRUE.equals(dto.getEnableChunked()) && dto.getChunkSize() != null
        && dto.getChunkSize() > 0) {
      entity.setTotalChunks((int) Math.ceil((double) dto.getFileSize() / dto.getChunkSize()));
    }
    return entity;
  }

  public static List<UploadTask> toBatchCreateEntities(BatchUploadCreateDto dto) {
    List<UploadTask> tasks = new ArrayList<>();
    for (UploadFileInfoDto file : dto.getFiles()) {
      UploadTask entity = new UploadTask();
      entity.setRepositoryId(dto.getRepositoryId());
      entity.setFileName(file.getFileName());
      entity.setFileSize(file.getFileSize());
      entity.setPath(file.getPath());
      entity.setVersion(file.getVersion());
      entity.setEnableChunked(false);
      tasks.add(entity);
    }
    return tasks;
  }

  public static UploadTaskVo toTaskVo(UploadTask entity) {
    if (entity == null) {
      return null;
    }
    UploadTaskVo vo = new UploadTaskVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setRepositoryName(entity.getRepositoryName());
    vo.setFileName(entity.getFileName());
    vo.setFileSize(entity.getFileSize());
    vo.setChecksum(entity.getChecksum());
    vo.setPath(entity.getPath());
    vo.setVersion(entity.getVersion());
    vo.setStatus(entity.getStatus());
    vo.setUploadToken(entity.getUploadToken());
    vo.setExpires(entity.getExpires());
    vo.setEnableChunked(entity.getEnableChunked());
    vo.setTotalChunks(entity.getTotalChunks());
    vo.setUploadedChunks(entity.getUploadedChunks());
    vo.setProgress(entity.getProgressPercent());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    return vo;
  }

  public static GenericSpecification<UploadTask> getSpecification(UploadTaskFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate")
        .matchSearchFields("fileName")
        .orderByFields("id", "fileName", "createdDate", "status", "fileSize")
        .inAndNotFields("status")
        .build();
    return new GenericSpecification<>(filters);
  }
}
