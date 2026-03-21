package cloud.xcan.angus.core.gm.interfaces.service.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "服务查询DTO")
public class ServiceFindDto {

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "搜索关键词（服务名称）", example = "用户")
  private String keyword;

  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "状态筛选", example = "UP", allowableValues = {"UP", "DOWN",
      "OUT_OF_SERVICE"})
  private String status;
}
