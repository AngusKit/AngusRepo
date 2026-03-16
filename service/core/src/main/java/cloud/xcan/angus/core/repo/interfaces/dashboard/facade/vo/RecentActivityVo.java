package cloud.xcan.angus.core.repo.interfaces.dashboard.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "最近活动")
public class RecentActivityVo implements Serializable {

  @Schema(description = "活动ID")
  private Long id;

  @Schema(description = "操作类型")
  private String action;

  @Schema(description = "目标类型")
  private String targetType;

  @Schema(description = "目标名称")
  private String targetName;

  @Schema(description = "操作用户")
  private String userName;

  @Schema(description = "描述")
  private String description;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "时间")
  private LocalDateTime createdDate;
}
