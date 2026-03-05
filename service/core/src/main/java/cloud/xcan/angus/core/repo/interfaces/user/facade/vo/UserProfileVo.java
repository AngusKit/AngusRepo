package cloud.xcan.angus.core.repo.interfaces.user.facade.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "用户个人信息")
public class UserProfileVo {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "租户ID")
  private Long tenantId;

  @Schema(description = "用户名")
  private String name;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "头像URL")
  private String avatar;

  @Schema(description = "角色")
  private String role;

  @Schema(description = "部门")
  private String department;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "加入日期")
  private LocalDateTime joinedDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "最后登录时间")
  private LocalDateTime lastLogin;

  @Schema(description = "偏好设置")
  private String preferences;

  @Schema(description = "通知设置")
  private String notificationSettings;
}
