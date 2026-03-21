package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "权限信息")
public class PermissionDto {

  @Schema(description = "资源标识")
  private String resource;

  @Schema(description = "资源名称", accessMode = Schema.AccessMode.READ_ONLY)
  private String resourceName;

  @Schema(description = "操作列表")
  private List<String> actions;

}
