package cloud.xcan.angus.core.gm.interfaces.application.facade.dto;

import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_CODE_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_DESC_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_KEY_LENGTH_X2;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_URL_LENGTH;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationType;
import cloud.xcan.angus.validator.Version;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "创建应用请求参数")
public class ApplicationCreateDto {

  @NotBlank
  @Length(max = MAX_CODE_LENGTH)
  @Schema(description = "应用编码", requiredMode = Schema.RequiredMode.REQUIRED)
  private String code;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String name;

  @NotBlank
  @Length(max = MAX_NAME_LENGTH)
  @Schema(description = "显示名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String displayName;

  @NotNull
  @Schema(description = "应用类型", requiredMode = Schema.RequiredMode.REQUIRED)
  private ApplicationType type;

  @Schema(description = "状态，默认启用")
  private EnabledStatus status;

  @NotEmpty
  @Version
  @Schema(description = "应用版本", requiredMode = RequiredMode.REQUIRED)
  private String version;

  @Length(max = MAX_KEY_LENGTH_X2)
  @Schema(description = "应用所属端ID")
  private String clientId;

  @Length(max = MAX_URL_LENGTH)
  @Schema(description = "应用URL")
  private String url;

  @Schema(description = "标签列表")
  private List<String> tags;

  @Schema(description = "排序顺序，值越小越靠前")
  private Integer sortOrder;

  @Length(max = MAX_DESC_LENGTH)
  @Schema(description = "描述")
  private String description;
}
