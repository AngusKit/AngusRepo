package cloud.xcan.angus.core.gm.interfaces.sms;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsTemplateFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTemplateStatusVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTemplateVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SmsTemplate", description = "短信模板管理 - 短信模板的创建、更新、启用/禁用等功能")
@Validated
@RestController
@RequestMapping("/api/v1/sms/templates")
public class SmsTemplateRest {

  @Resource
  private SmsTemplateFacade smsTemplateFacade;

  @Operation(operationId = "createSmsTemplate", summary = "创建短信模板", description = "创建新的短信模板")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<SmsTemplateVo> createTemplate(
      @Valid @RequestBody SmsTemplateCreateDto dto) {
    return ApiLocaleResult.success(smsTemplateFacade.createTemplate(dto));
  }

  @Operation(operationId = "updateSmsTemplate", summary = "更新短信模板", description = "更新指定短信模板")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<SmsTemplateVo> updateTemplate(
      @Parameter(description = "模板ID") @PathVariable Long id,
      @Valid @RequestBody SmsTemplateUpdateDto dto) {
    return ApiLocaleResult.success(smsTemplateFacade.updateTemplate(id, dto));
  }

  @Operation(operationId = "updateSmsTemplateStatus", summary = "启用/禁用短信模板", description = "修改短信模板状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}/status")
  public ApiLocaleResult<SmsTemplateStatusVo> updateTemplateStatus(
      @Parameter(description = "模板ID") @PathVariable Long id,
      @Valid @RequestBody EnabledStatusUpdateDto dto) {
    return ApiLocaleResult.success(smsTemplateFacade.updateTemplateStatus(id, dto));
  }

  @Operation(operationId = "deleteSmsTemplate", summary = "删除短信模板", description = "删除指定短信模板")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void deleteTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
    smsTemplateFacade.deleteTemplate(id);
  }

  @Operation(operationId = "getSmsTemplates", summary = "获取短信模板列表", description = "分页获取短信模板列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<SmsTemplateVo>> listTemplates(
      @Valid @ParameterObject SmsTemplateFindDto dto) {
    return ApiLocaleResult.success(smsTemplateFacade.listTemplates(dto));
  }
}
