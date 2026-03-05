package cloud.xcan.angus.core.repo.interfaces.activitylog.facade.dto;

import cloud.xcan.angus.core.repo.domain.activitylog.ActivityAction;
import cloud.xcan.angus.core.repo.domain.activitylog.ActivityCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 创建活动日志DTO
 */
@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建活动日志请求参数")
public class ActivityLogCreateDto {

  @NotNull
  @Schema(description = "操作类型", required = true)
  private ActivityAction action;

  @NotBlank
  @Schema(description = "操作用户", required = true)
  private String user;

  @NotBlank
  @Schema(description = "操作对象", required = true)
  private String artifact;

  @NotBlank
  @Schema(description = "仓库名称", required = true)
  private String repository;

  @Schema(description = "IP地址")
  private String ipAddress;

  @Schema(description = "User Agent")
  private String userAgent;

  @Schema(description = "详细信息")
  private String details;

  @Schema(description = "分类")
  private ActivityCategory category;
}
