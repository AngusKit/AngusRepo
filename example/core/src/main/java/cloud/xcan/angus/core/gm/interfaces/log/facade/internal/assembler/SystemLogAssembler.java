package cloud.xcan.angus.core.gm.interfaces.log.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatFileSize;

import cloud.xcan.angus.core.gm.domain.log.SystemLog;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.SystemLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.SystemLogListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

/**
 * 系统日志数据组装器
 */
public class SystemLogAssembler {

  public static SystemLogDetailVo toDetailVo(SystemLog log) {
    SystemLogDetailVo vo = new SystemLogDetailVo();
    vo.setId(log.getId());
    vo.setFilename(log.getFilename());
    vo.setFilePath(log.getFilePath());
    vo.setSize(log.getSize());
    vo.setSizeFormatted(formatFileSize(log.getSize()));
    vo.setLineCount(log.getLineCount());
    vo.setType(log.getType());
    vo.setDate(log.getDate());
    vo.setApplicationId(log.getApplicationId());
    vo.setStatus(log.getStatus());
    vo.setCompressed(log.getCompressed());
    vo.setEncoding(log.getEncoding());
    vo.setCreatedDate(log.getCreatedDate());
    return vo;
  }

  public static SystemLogListVo toListVo(SystemLog log) {
    SystemLogListVo vo = new SystemLogListVo();
    vo.setId(log.getId());
    vo.setFilename(log.getFilename());
    vo.setSize(log.getSize());
    vo.setSizeFormatted(formatFileSize(log.getSize()));
    vo.setLineCount(log.getLineCount());
    vo.setType(log.getType());
    vo.setDate(log.getDate());
    vo.setApplicationId(log.getApplicationId());
    vo.setStatus(log.getStatus());
    vo.setCompressed(log.getCompressed());
    vo.setCreatedDate(log.getCreatedDate());
    return vo;
  }

  public static GenericSpecification<SystemLog> getSpecification(SystemLogFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "date", "startDate", "endDate", "createdDate")
        .matchSearchFields("filename")
        .orderByFields("id", "date", "filename", "createdDate")
        .build();
    return new GenericSpecification<>(filters);
  }

}
