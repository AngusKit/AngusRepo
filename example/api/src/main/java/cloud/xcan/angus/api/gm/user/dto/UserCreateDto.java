package cloud.xcan.angus.api.gm.user.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ADDRESS_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.api.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
@Schema(description = "创建用户DTO")
public class UserCreateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "zhangsan")
  private String username;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
  private String name;

  @Email
  @NotBlank
  @Length(max = MAX_EMAIL_LENGTH)
  @Schema(description = "邮箱", example = "zhangsan@example.com")
  private String email;

  @Length(max = MAX_MOBILE_LENGTH)
  @Schema(description = "手机号", example = "13800138000")
  private String phone;

  @NotEmpty
  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String password;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "头像URL", example = "https://example.com/avatar.png")
  private String avatar;

  @Schema(description = "性别")
  private Gender gender;

  @Length(max = MAX_KEY_LENGTH)
  @Schema(description = "座机")
  private String landline;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "职位")
  private String jobTitle;

  @Length(max = MAX_ADDRESS_LENGTH)
  @Schema(description = "地址")
  private String address;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "角色ID列表")
  private List<Long> roleIds;

  @Schema(description = "状态")
  private UserStatus status;
}
