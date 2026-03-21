package cloud.xcan.angus.core.gm.interfaces.department.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH_X4;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@Schema(description = "创建部门请求参数")
public class DepartmentCreateDto {

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "部门名称", requiredMode = RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "部门编码", requiredMode = RequiredMode.REQUIRED)
  private String code;

  @Schema(description = "父部门ID")
  private Long parentId;

  @Schema(description = "负责人ID")
  private Long leaderId;

  @Length(max = MAX_DESC_LENGTH_X4)
  @Schema(description = "描述")
  private String description;

  @Schema(description = "排序")
  private Integer sortOrder;

  @Schema(description = "状态，默认启用")
  private EnabledStatus status;
}
