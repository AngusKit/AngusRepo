package cloud.xcan.angus.core.repo.interfaces.activity.facade.vo;


import cloud.xcan.angus.api.commonlink.CombinedTargetType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Setter
@Getter
@Accessors(chain = true)
@Schema(description = "活动记录详情")
public class ActivityDetailVo implements Serializable {

  @Schema(description = "活动记录ID")
  private Long id;

  @Schema(description = "项目ID")
  private Long projectId;

  @Schema(description = "项目名称")
  private String projectName;

  @Schema(description = "操作用户ID")
  private Long userId;

  @Schema(description = "操作用户姓名")
  private String fullName;

  @Schema(description = "操作用户头像")
  private String avatar;

  @Schema(description = "目标资源ID")
  private Long targetId;

  @Schema(description = "父目标资源ID")
  private Long parentTargetId;

  @Schema(description = "目标资源类型")
  private CombinedTargetType targetType;

  @Schema(description = "目标资源名称")
  private String targetName;

  @Schema(description = "操作时间")
  private LocalDateTime optDate;

  @Schema(description = "操作描述")
  private String description;

  @Schema(description = "操作详情")
  private String detail;

}



