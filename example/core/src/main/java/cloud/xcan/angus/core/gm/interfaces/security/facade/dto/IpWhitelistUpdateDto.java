package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.IPV4_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "更新IP白名单DTO")
public class IpWhitelistUpdateDto {

  @Length(max = IPV4_LENGTH)
  @Schema(description = "IP地址")
  private String ipAddress;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "IP范围")
  private String ipRange;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "描述")
  private String description;

  @Schema(description = "状态")
  private EnabledStatus status;
}
