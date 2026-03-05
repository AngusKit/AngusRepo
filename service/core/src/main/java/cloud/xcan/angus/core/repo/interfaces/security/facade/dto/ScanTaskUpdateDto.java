package cloud.xcan.angus.core.repo.interfaces.security.facade.dto;

import cloud.xcan.angus.core.repo.domain.security.ScanType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "更新扫描任务请求参数")
public class ScanTaskUpdateDto {

  @Schema(description = "扫描类型")
  private ScanType scanType;
}
