package cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo;

import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapSyncStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "LDAP同步结果VO")
public class LdapSyncResultVo implements Serializable {

  @Schema(description = "同步记录ID")
  private String id;

  @Schema(description = "开始时间")
  private LocalDateTime startTime;

  @Schema(description = "结束时间")
  private LocalDateTime endTime;

  @Schema(description = "状态")
  private LdapSyncStatus status;

  @Schema(description = "总用户数")
  private Integer totalUsers;

  @Schema(description = "新增用户数")
  private Integer newUsers;

  @Schema(description = "更新用户数")
  private Integer updatedUsers;

  @Schema(description = "删除用户数")
  private Integer deletedUsers;

  @Schema(description = "失败用户数")
  private Integer failedUsers;
}
