package cloud.xcan.angus.core.repo.interfaces.repository.facade.vo;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryStatus;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryType;
import cloud.xcan.angus.remote.NameJoinField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "仓库详情")
public class RepositoryDetailVo implements Serializable {

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

  @Schema(description = "创建人ID")
  private Long createdBy;

  @Schema(description = "创建人姓名")
  @NameJoinField(id = "createdBy", repository = "commonUserBaseRepo")
  private String creatorName;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;

  @Schema(description = "修改人ID")
  private Long modifiedBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
