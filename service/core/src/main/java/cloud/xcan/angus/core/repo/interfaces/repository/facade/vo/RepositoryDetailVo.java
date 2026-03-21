package cloud.xcan.angus.core.repo.interfaces.repository.facade.vo;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryStatus;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryType;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "仓库详情")
public class RepositoryDetailVo extends TenantAuditingVo {

  @Schema(description = "仓库ID")
  private Long id;

  @Schema(description = "租户ID")
  private Long tenantId;

  @Schema(description = "仓库名称")
  private String name;

  @Schema(description = "仓库格式")
  private RepositoryFormat format;

  @Schema(description = "仓库类型")
  private RepositoryType type;

  @Schema(description = "仓库描述")
  private String description;

  @Schema(description = "制品数量")
  private Integer artifacts;

  @Schema(description = "存储大小（字节）")
  private Long sizeBytes;

  @Schema(description = "仓库访问URL")
  private String url;

  @Schema(description = "仓库状态")
  private RepositoryStatus status;

  @Schema(description = "远程仓库URL")
  private String remoteUrl;

  @Schema(description = "Blob存储")
  private String blobStore;

  @Schema(description = "仓库设置")
  private String settings;
}
