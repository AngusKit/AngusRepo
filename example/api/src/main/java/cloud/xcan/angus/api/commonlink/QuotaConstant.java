package cloud.xcan.angus.api.commonlink;

public interface QuotaConstant {

  /**
   * 租户数量配额编码
   */
  String QuotaTenantCount = "TenantCount";
  /**
   * 用户数量配额编码
   */
  String QuotaUserCount = "UserCount";
  /**
   * 用户组数量配额编码
   */
  String QuotaGroupCount = "GroupCount";
  /**
   * 部门数量配额编码
   */
  String QuotaDepartmentCount = "DepartmentCount";
  /**
   * 存储空间配额编码
   */
  String QuotaStorageSpace = "StorageSpace";
  /**
   * 自定义应用数量配额编码
   */
  String QuotaCustomApplications = "CustomApplicationCount";
  /**
   * API调用次数配额编码
   */
  String QuotaApiCalls = "ApiCalls";
}
