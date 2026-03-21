package cloud.xcan.angus.core.gm.interfaces.security.facade.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通知接收用户（含ID和名称，用于回显）")
public class RecipientUserDto {

  @Schema(description = "用户ID", example = "1")
  private Long id;

  @Schema(description = "用户名称", example = "张三")
  private String name;
}
