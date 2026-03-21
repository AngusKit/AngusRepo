package cloud.xcan.angus.core.gm.interfaces.backup;

import cloud.xcan.angus.core.gm.interfaces.backup.facade.BackupSettingsFacade;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.dto.BackupSettingsUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.backup.facade.vo.BackupSettingsVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "BackupSettings", description = "备份设置管理 - 备份存储配置和策略管理")
@Validated
@RestController
@RequestMapping("/api/v1/backup/settings")
public class BackSettingsRest {

  @Resource
  private BackupSettingsFacade backupSettingsFacade;

  @Operation(operationId = "updateBackupSettings", summary = "更新备份设置",
      description = "更新备份存储配置和策略")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份设置更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping
  public ApiLocaleResult<BackupSettingsVo> update(
      @Valid @RequestBody BackupSettingsUpdateDto dto) {
    return ApiLocaleResult.success(backupSettingsFacade.update(dto));
  }

  @Operation(operationId = "getBackupSettings", summary = "查询备份设置",
      description = "查询当前备份存储配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "备份设置查询成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<BackupSettingsVo> getSettings() {
    return ApiLocaleResult.success(backupSettingsFacade.getSettings());
  }
}
