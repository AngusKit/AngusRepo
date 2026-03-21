package cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto;

import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapSyncStatus;
import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "LDAP同步历史查询DTO")
public class LdapSyncHistoryFindDto extends PageQuery {

  @Schema(description = "状态筛选", example = "RUNNING")
  private LdapSyncStatus status;

  @Override
  public String getDefaultOrderBy() {
    return "createdDate";
  }
}
