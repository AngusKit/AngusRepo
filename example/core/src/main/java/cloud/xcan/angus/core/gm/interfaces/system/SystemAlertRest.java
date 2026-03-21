package cloud.xcan.angus.core.gm.interfaces.system;

import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemAlertFacade;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.AlertRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.AlertRuleCreateDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AlertRecordVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.AlertRuleSettingsVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SystemAlert", description = "系统监控 - 系统资源监控、性能指标、告警管理")
@Validated
@RestController
@RequestMapping("/api/v1/system/alert")
public class SystemAlertRest {

  @Resource
  private SystemAlertFacade systemAlertFacade;

  @Operation(operationId = "getAlertRules", summary = "获取告警规则设置", description = "获取告警规则配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/rules")
  public ApiLocaleResult<AlertRuleSettingsVo> getAlertRules() {
    return ApiLocaleResult.success(systemAlertFacade.getSettings());
  }

  @Operation(operationId = "updateAlertRules", summary = "更新告警规则设置", description = "更新告警规则配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/rules")
  public ApiLocaleResult<AlertRuleSettingsVo> updateAlertRules(
      @Valid @RequestBody List<AlertRuleCreateDto> dto) {
    return ApiLocaleResult.success(systemAlertFacade.update(dto));
  }

  @Operation(operationId = "getAlertRecords", summary = "获取告警记录列表", description = "分页获取告警记录列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/records")
  public ApiLocaleResult<PageResult<AlertRecordVo>> listAlertRecords(
      @ParameterObject AlertRecordFindDto dto) {
    return ApiLocaleResult.success(systemAlertFacade.listAlertRecords(dto));
  }
}
