package cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import cloud.xcan.angus.core.repo.domain.artifact.ArtifactFormat;
import cloud.xcan.angus.remote.NameJoinField;
import cloud.xcan.angus.remote.vo.TenantAuditingVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "制品详情")
public class ArtifactDetailVo extends TenantAuditingVo {

  @Schema(description = "制品ID")
  private Long id;

  @Schema(description = "仓库ID")
  private Long repositoryId;

  @Schema(description = "仓库名称")
  private String repositoryName;

  @Schema(description = "制品格式")
  private ArtifactFormat format;

  @Schema(description = "制品名称")
  private String name;

  @Schema(description = "制品路径")
  private String path;

  @Schema(description = "制品版本")
  private String version;

  @Schema(description = "制品描述")
  private String description;

  @Schema(description = "文件大小（字节）")
  private Long sizeBytes;

  @Schema(description = "校验和")
  private String checksum;

  @Schema(description = "下载次数")
  private Integer downloads;

  @Schema(description = "收藏数")
  private Integer stars;

  @Schema(description = "许可证")
  private String license;

  @Schema(description = "是否最新版本")
  private Boolean isLatest;

  @Schema(description = "标签")
  private String tags;

  @Schema(description = "版本列表")
  private String versions;

  @Schema(description = "漏洞信息")
  private String vulnerability;

  @Schema(description = "元数据")
  private String metadata;
}

