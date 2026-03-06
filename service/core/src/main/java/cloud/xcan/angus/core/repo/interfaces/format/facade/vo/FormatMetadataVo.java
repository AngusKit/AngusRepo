package cloud.xcan.angus.core.repo.interfaces.format.facade.vo;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "格式元数据详情")
public class FormatMetadataVo {

  @Schema(description = "元数据ID")
  private Long id;

  @Schema(description = "仓库ID")
  private Long repositoryId;

  @Schema(description = "仓库格式")
  private RepositoryFormat format;

  @Schema(description = "制品名称")
  private String name;

  @Schema(description = "版本")
  private String version;

  @Schema(description = "描述")
  private String description;

  @Schema(description = "格式特定元数据（JSON）")
  private String metadata;

  @Schema(description = "创建人ID")
  private Long createdBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;

  @Schema(description = "修改人ID")
  private Long modifiedBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "修改时间")
  private LocalDateTime modifiedDate;
}
