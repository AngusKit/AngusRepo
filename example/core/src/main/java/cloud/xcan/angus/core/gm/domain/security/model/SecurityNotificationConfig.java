package cloud.xcan.angus.core.gm.domain.security.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "安全通知配置")
public class SecurityNotificationConfig extends SecurityConfig {

  @Schema(description = "用户关键操作触发邮件通知", example = "true")
  private Boolean userCriticalOperationNotify = true;

  @Schema(description = "系统负载过高时通知（资源使用率超过85%）", example = "true")
  private Boolean systemLoadHighNotify = true;

  @Schema(description = "系统服务组件状态异常时通知", example = "true")
  private Boolean serviceComponentAbnormalNotify = false;

  @Schema(description = "用户登录失败时通知", example = "true")
  private Boolean loginFailureNotify = false;

  @Schema(description = "新用户注册成功通知", example = "true")
  private Boolean newUserRegisterNotify = false;

  @Schema(description = "通知接收用户列表（含ID和名称）", example = "[{\"id\":1,\"name\":\"张三\"}]")
  private List<RecipientUser> recipientUsers = new ArrayList<>();

  /**
   * 兼容旧数据：反序列化时 recipientUserIds 转为 recipientUsers
   */
  @JsonProperty("recipientUserIds")
  public void setRecipientUserIdsCompat(List<Long> ids) {
    if (ids != null && !ids.isEmpty()) {
      this.recipientUsers = ids.stream()
          .map(id -> new RecipientUser(id, null))
          .collect(Collectors.toList());
    }
  }
}
