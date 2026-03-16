package cloud.xcan.angus.core.repo.interfaces.upload.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

import cloud.xcan.angus.core.repo.domain.upload.UploadStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "上传任务详情")
public class UploadTaskVo implements Serializable {

  @Schema(description = "任务ID")
  private Long id;

  @Schema(description = "仓库ID")
  private Long repositoryId;

  @Schema(description = "仓库名称")
  private String repositoryName;

  @Schema(description = "文件名称")
  private String fileName;

  @Schema(description = "文件大小（字节）")
  private Long fileSize;

  @Schema(description = "文件校验和")
  private String checksum;

  @Schema(description = "上传路径")
  private String path;

  @Schema(description = "制品版本号")
  private String version;

  @Schema(description = "上传状态")
  private UploadStatus status;

  @Schema(description = "上传URL")
  private String uploadUrl;

  @Schema(description = "上传令牌")
  private String uploadToken;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "过期时间")
  private LocalDateTime expires;

  @Schema(description = "是否启用分片上传")
  private Boolean enableChunked;

  @Schema(description = "总分片数")
  private Integer totalChunks;

  @Schema(description = "已上传分片数")
  private Integer uploadedChunks;

  @Schema(description = "上传进度（百分比）")
  private Integer progress;

  @Schema(description = "创建人ID")
  private Long createdBy;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "创建时间")
  private LocalDateTime createdDate;
}
