package cloud.xcan.angus.core.repo.interfaces.system.facade.vo;

import static cloud.xcan.angus.spec.SpecConstant.DateFormat.DEFAULT_DATE_TIME_FORMAT;

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
@Schema(description = "许可证信息")
public class LicenseInfoVo implements Serializable {

  @Schema(description = "许可证类型")
  private String licenseType;

  @Schema(description = "许可给")
  private String licenseTo;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "签发日期")
  private LocalDateTime issuedDate;

  @JsonFormat(pattern = DEFAULT_DATE_TIME_FORMAT)
  @Schema(description = "过期日期")
  private LocalDateTime expiresAt;

  @Schema(description = "最大用户数")
  private Integer maxUsers;

  @Schema(description = "最大仓库数")
  private Integer maxRepositories;

  @Schema(description = "最大存储（字节）")
  private Long maxStorage;

  @Schema(description = "功能列表")
  private String features;

  @Schema(description = "是否有效")
  private Boolean valid;
}
