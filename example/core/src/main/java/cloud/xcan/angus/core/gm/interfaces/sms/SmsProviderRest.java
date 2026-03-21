package cloud.xcan.angus.core.gm.interfaces.sms;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsProviderFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsProviderUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsProviderVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SmsProviderExtension", description = "短信服务商管理 - 短信服务商配置的更新、查询等功能")
@Validated
@RestController
@RequestMapping("/api/v1/sms/providers")
public class SmsProviderRest {

  @Resource
  private SmsProviderFacade smsProviderFacade;

  @Operation(operationId = "updateSmsProvider", summary = "更新服务商配置", description = "更新指定服务商配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<SmsProviderVo> updateProvider(
      @Parameter(description = "服务商ID") @PathVariable Long id,
      @Valid @RequestBody SmsProviderUpdateDto dto) {
    return ApiLocaleResult.success(smsProviderFacade.updateProvider(id, dto));
  }

  @Operation(operationId = "updateSmsProviderStatus", summary = "启用/禁用服务商", description = "更新服务商启用状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/status")
  public ApiLocaleResult<SmsProviderVo> updateProviderStatus(
      @Parameter(description = "服务商ID") @PathVariable Long id,
      @Valid @RequestBody EnabledStatusUpdateDto dto) {
    return ApiLocaleResult.success(smsProviderFacade.updateProviderStatus(id, dto));
  }

  @Operation(operationId = "setDefaultSmsProvider", summary = "设置默认服务商", description = "设置指定服务商为默认服务商")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "设置成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/default")
  public ApiLocaleResult<SmsProviderVo> setDefaultProvider(
      @Parameter(description = "服务商ID") @PathVariable Long id) {
    return ApiLocaleResult.success(smsProviderFacade.setDefaultProvider(id));
  }

  @Operation(operationId = "getSmsProvider", summary = "获取服务商详情", description = "获取指定服务商的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiLocaleResult<SmsProviderVo> getProvider(
      @Parameter(description = "服务商ID") @PathVariable Long id) {
    return ApiLocaleResult.success(smsProviderFacade.getProvider(id));
  }

  @Operation(operationId = "getSmsProviders", summary = "获取短信服务商配置", description = "获取所有短信服务商配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<List<SmsProviderVo>> listProviders() {
    return ApiLocaleResult.success(smsProviderFacade.listProviders());
  }

}
