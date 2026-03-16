package cloud.xcan.angus.core.repo.interfaces.system.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import java.io.Serializable;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_NAME_LENGTH_X2;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "通用设置更新请求参数")
public class GeneralSettingsUpdateDto implements Serializable {

  @Size(max = MAX_NAME_LENGTH_X2)
  @Schema(description = "站点名称")
  private String siteName;

  @Size(max = 500)
  @Schema(description = "站点URL")
  private String siteUrl;

  @Schema(description = "是否允许注册")
  private Boolean allowRegistration;

  @Schema(description = "是否允许匿名访问")
  private Boolean allowAnonymousAccess;

  @Schema(description = "默认仓库存储容量（字节）")
  private Long defaultStorageQuota;
}
