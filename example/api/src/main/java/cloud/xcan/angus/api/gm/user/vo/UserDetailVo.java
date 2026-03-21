package cloud.xcan.angus.api.gm.user.vo;

import cloud.xcan.angus.api.commonlink.department.DepartmentInfo;
import cloud.xcan.angus.api.commonlink.group.GroupInfo;
import cloud.xcan.angus.api.commonlink.role.RoleInfo;
import cloud.xcan.angus.api.commonlink.user.enums.UserSource;
import cloud.xcan.angus.api.commonlink.user.enums.UserStatus;
import cloud.xcan.angus.api.enums.Gender;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户详情")
public class UserDetailVo extends TenantAuditingVo {

  @Schema(description = "ID")
  private Long id;

  @Schema(description = "用户名")
  private String username;

  @Schema(description = "姓名")
  private String name;

  @Schema(description = "邮箱")
  private String email;

  @Schema(description = "邮箱是否已验证")
  private Boolean emailVerified = false;

  @Schema(description = "手机号")
  private String phone;

  @Schema(description = "手机号是否已验证")
  private Boolean phoneVerified = false;

  @Schema(description = "头像")
  private String avatar;

  @Schema(description = "性别")
  private Gender gender;

  @Schema(description = "座机")
  private String landline;

  @Schema(description = "职位")
  private String jobTitle;

  @Schema(description = "地址")
  private String address;

  @Schema(description = "地区")
  private String location;

  @Schema(description = "个人简介")
  private String bio;

  @Schema(description = "个人网站")
  private String website;

  @Schema(description = "GitHub")
  private String github;

  @Schema(description = "Twitter")
  private String twitter;

  @Schema(description = "LinkedIn")
  private String linkedin;

  @Schema(description = "部门ID（主部门）")
  private Long departmentId;

  @Schema(description = "部门（主部门）")
  @NameJoinField(id = "departmentId", repository = "commonDepartmentRepo")
  private String department;

  @Schema(description = "状态")
  private UserStatus status;

  @Schema(description = "租户管理员")
  private Boolean sysAdmin;

  @Schema(description = "是否锁定")
  private Boolean locked;

  @Schema(description = "用户来源")
  private UserSource source;

  @Schema(description = "LDAP配置ID")
  private Long ldapId;

  @Schema(description = "最后登录时间")
  private LocalDateTime lastLogin;

  @Schema(description = "是否在线")
  private Boolean online;

  @Schema(description = "在线时间")
  private LocalDateTime onlineDate;

  @Schema(description = "离线时间")
  private LocalDateTime offlineDate;

  @Schema(description = "角色列表")
  private List<RoleInfo> roles;

  @Schema(description = "部门列表（所有部门）")
  private List<DepartmentInfo> departments;

  @Schema(description = "用户组列表")
  private List<GroupInfo> groups;

  @Schema(description = "最近10次登录历史")
  private List<LoginHistoryVo> loginHistories;

}
