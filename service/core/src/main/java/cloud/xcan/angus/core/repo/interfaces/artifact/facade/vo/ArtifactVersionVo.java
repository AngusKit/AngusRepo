package cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "制品版本信息")
public class ArtifactVersionVo {

  @Schema(description = "制品ID")
  private Long id;

  @Schema(description = "版本号")
  private String version;

  @Schema(description = "文件大小（字节）")
  private Long sizeBytes;

  @Schema(description = "校验和")
  private String checksum;

  @Schema(description = "下载次数")
  private Integer downloads;

  @Schema(description = "是否最新版本")
  private Boolean isLatest;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;
}
