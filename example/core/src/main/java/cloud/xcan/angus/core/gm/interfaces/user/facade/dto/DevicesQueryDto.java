package cloud.xcan.angus.core.gm.interfaces.user.facade.dto;

import cloud.xcan.angus.remote.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(description = "查询登录设备请求参数")
public class DevicesQueryDto extends PageQuery {

  @Override
  public String getDefaultOrderBy() {
    return "lastActiveAt";
  }
}
