package cloud.xcan.angus.core.gm.interfaces.user.facade.vo;

import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户个人信息详情")
public class UserProfileVo extends TenantAuditingVo {

  @Schema(description = "用户ID")
  private Long id;

  @Schema(description = "用户名")
  private String username;

  @Schema(description = "姓名")
  private String name;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "手机号")
  private String phone;

  @Schema(description = "头像URL")
  private String avatar;

  @Schema(description = "性别")
  private Gender gender;

  @Schema(description = "座机")
  private String landline;

  @Schema(description = "个人简介")
  private String bio;

  @Schema(description = "职位")
  private String jobTitle;

  @Schema(description = "地址")
  private String address;

  @Schema(description = "地区")
  private String location;

  @Schema(description = "部门")
  @NameJoinField(id = "departmentId", repository = "commonDepartmentRepo")
  private String department;

  @Schema(description = "部门ID")
  private Long departmentId;

  @Schema(description = "个人网站")
  private String website;

  @Schema(description = "GitHub")
  private String github;

  @Schema(description = "Twitter")
  private String twitter;

  @Schema(description = "LinkedIn")
  private String linkedin;
}
