package cloud.xcan.angus.api.gm.user.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;

import cloud.xcan.angus.api.commonlink.user.enums.InviteType;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "邀请用户请求参数")
public class UserInviteDto {

  @Valid
  @Size(max = 200)
  @Schema(description = "邮箱列表，邮件邀请时必须填写，支持批量邀请多个邮箱")
  private List<@Email String> emails;

  @NotNull
  @Schema(description = "邀请方式", requiredMode = RequiredMode.REQUIRED, example = "LINK")
  private InviteType inviteType;

  @Schema(description = "邀请应用ID")
  private Long appId;

  @Schema(description = "角色ID")
  private Long roleId;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "邀请消息")
  private String message;

  @Schema(description = "过期天数", defaultValue = "7")
  private Integer expireDays = 7;

}
