package cloud.xcan.angus.core.repo.interfaces.activitylog.facade.internal.assembler;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityLog;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogCreateDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto.ActivityLogFindDto;
import cloud.xcan.angus.core.repo.interfaces.activitylog.facade.vo.ActivityLogVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * 活动日志组装器
 */
public class ActivityLogAssembler {

  /**
   * 将DTO转换为实体
   */
  public static ActivityLog toEntity(ActivityLogCreateDto dto) {
    ActivityLog activityLog = new ActivityLog();
    activityLog.setId(UUID.randomUUID().toString());
    activityLog.setAction(dto.getAction());
    activityLog.setUser(dto.getUser());
    activityLog.setArtifact(dto.getArtifact());
    activityLog.setRepository(dto.getRepository());
    activityLog.setTimestamp(LocalDateTime.now());
    activityLog.setIpAddress(dto.getIpAddress());
    activityLog.setUserAgent(dto.getUserAgent());
    activityLog.setDetails(dto.getDetails());
    activityLog.setCategory(dto.getCategory() != null ? dto.getCategory() :
        cloud.xcan.angus.core.repo.domain.activitylog.ActivityCategory.SYSTEM);
    return activityLog;
  }

  /**
   * 将实体转换为VO
   */
  public static ActivityLogVo toVo(ActivityLog activityLog) {
    if (activityLog == null) {
      return null;
    }

    ActivityLogVo vo = new ActivityLogVo();
    // 审计字段
    if (activityLog.getTenantId() != null) {
      vo.setTenantId(activityLog.getTenantId().toString());
    }
    vo.setId(activityLog.getId());
    vo.setAction(activityLog.getAction());
    vo.setUser(activityLog.getUser());
    vo.setArtifact(activityLog.getArtifact());
    vo.setRepository(activityLog.getRepository());
    vo.setTimestamp(activityLog.getTimestamp());
    vo.setIpAddress(activityLog.getIpAddress());
    vo.setUserAgent(activityLog.getUserAgent());
    vo.setDetails(activityLog.getDetails());
    vo.setCategory(activityLog.getCategory());

    return vo;
  }

  /**
   * 获取查询规格
   */
  public static GenericSpecification<ActivityLog> getSpecification(ActivityLogFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("timestamp", "createdDate")
        .orderByFields("timestamp", "createdDate")
        .matchSearchFields("artifact", "user", "repository")
        .inAndNotFields("action", "category", "user", "repository")
        .build();
    return new GenericSpecification<>(filters);
  }
}
