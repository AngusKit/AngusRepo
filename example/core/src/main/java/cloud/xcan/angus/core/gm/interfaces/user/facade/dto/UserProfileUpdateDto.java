package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_ADDRESS_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X4;

import cloud.xcan.angus.api.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新个人信息请求参数")
public class UserProfileUpdateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "用户姓名", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @Schema(description = "性别")
  private Gender gender;

  @Length(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "个人简介")
  private String bio;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "职位")
  private String jobTitle;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "地区")
  private String location;

  @Length(max = MAX_ADDRESS_LENGTH)
  @Schema(description = "地址")
  private String address;

  @URL
  @Length(max = MAX_NAME_LENGTH_X4)
  @Schema(description = "个人网站")
  private String website;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "GitHub")
  private String github;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "Twitter")
  private String twitter;

  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "LinkedIn")
  private String linkedin;

}
