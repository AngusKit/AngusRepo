package cloud.xcan.angus.api.commonlink;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "趋势方向枚举")
public enum TrendEnum {
  UP,
  DOWN,
  FLAT
}
