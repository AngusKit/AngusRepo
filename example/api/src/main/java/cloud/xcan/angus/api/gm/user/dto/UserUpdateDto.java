package cloud.xcan.angus.api.gm.user.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ADDRESS_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_EMAIL_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_MOBILE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH_X2;

import cloud.xcan.angus.api.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Data
@Accessors(chain = true)
@Schema(description = "更新用户DTO")
public class UserUpdateDto {

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
  @Schema(description = "手机号")
  private String phone;

  @Length(max = MAX_URL_LENGTH_X2)
  @Schema(description = "头像URL")
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

}
