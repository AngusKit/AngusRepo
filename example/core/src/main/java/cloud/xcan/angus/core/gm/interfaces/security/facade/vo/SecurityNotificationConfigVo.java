package cloud.xcan.angus.core.gm.interfaces.security.facade.vo;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.interfaces.security.facade.dto.RecipientUserDto;
import cloud.xcan.angus.remote.vo.AuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "安全通知配置VO")
public class SecurityNotificationConfigVo extends AuditingVo {

  @Schema(description = "配置ID", example = "1")
  private Long id;

  @Schema(description = "是否启用", example = "true")
  private EnabledStatus status;

  @Schema(description = "用户关键操作触发邮件通知", example = "true")
  private Boolean userCriticalOperationNotify;

  @Schema(description = "系统负载过高时通知（资源使用率超过85%）", example = "true")
  private Boolean systemLoadHighNotify;

  @Schema(description = "系统服务组件状态异常时通知", example = "true")
  private Boolean serviceComponentAbnormalNotify;

  @Schema(description = "用户登录失败时通知", example = "true")
  private Boolean loginFailureNotify;

  @Schema(description = "新用户注册成功通知", example = "true")
  private Boolean newUserRegisterNotify;

  @Schema(description = "通知接收用户列表（含ID和名称）", example = "[{\"id\":1,\"name\":\"张三\"}]")
  private List<RecipientUserDto> recipientUsers;
}
