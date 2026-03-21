package cloud.xcan.angus.core.gm.interfaces.email;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailTemplateFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateCreateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailTemplateUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateStatusVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTemplateVo;
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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EmailTemplate", description = "邮件模板管理 - 邮件模板的创建、更新、启用/禁用等功能")
@Validated
@RestController
@RequestMapping("/api/v1/email/templates")
public class EmailTemplateRest {

  @Resource
  private EmailTemplateFacade emailTemplateFacade;

  @Operation(operationId = "createEmailTemplate", summary = "创建邮件模板", description = "创建新的邮件模板")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  public ApiLocaleResult<EmailTemplateVo> createTemplate(
      @Valid @RequestBody EmailTemplateCreateDto dto) {
    return ApiLocaleResult.success(emailTemplateFacade.createTemplate(dto));
  }

  @Operation(operationId = "updateEmailTemplate", summary = "更新邮件模板", description = "更新指定邮件模板")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/{id}")
  public ApiLocaleResult<EmailTemplateVo> updateTemplate(
      @Parameter(description = "模板ID") @PathVariable Long id,
      @Valid @RequestBody EmailTemplateUpdateDto dto) {
    return ApiLocaleResult.success(emailTemplateFacade.updateTemplate(id, dto));
  }

  @Operation(operationId = "updateEmailTemplateStatus", summary = "启用/禁用邮件模板", description = "修改邮件模板状态")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/{id}/status")
  public ApiLocaleResult<EmailTemplateStatusVo> updateTemplateStatus(
      @Parameter(description = "模板ID") @PathVariable Long id,
      @Valid @RequestBody EnabledStatusUpdateDto dto) {
    return ApiLocaleResult.success(emailTemplateFacade.updateTemplateStatus(id, dto));
  }

  @Operation(operationId = "deleteEmailTemplate", summary = "删除邮件模板", description = "删除指定邮件模板")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  public void deleteTemplate(@Parameter(description = "模板ID") @PathVariable Long id) {
    emailTemplateFacade.deleteTemplate(id);
  }

  @Operation(operationId = "getEmailTemplates", summary = "获取邮件模板列表", description = "分页获取邮件模板列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<PageResult<EmailTemplateVo>> listTemplates(
      @Valid @ParameterObject EmailTemplateFindDto dto) {
    return ApiLocaleResult.success(emailTemplateFacade.listTemplates(dto));
  }
}
