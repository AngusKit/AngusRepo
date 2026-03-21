package cloud.xcan.angus.core.gm.interfaces.email;

import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailSmtpFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpTestDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSmtpUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpTestVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailSmtpVo;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EmailSmtp", description = "SMTP配置管理 - SMTP服务器配置、连接测试")
@Validated
@RestController
@RequestMapping("/api/v1/email/smtp")
public class EmailSmtpRest {

  @Resource
  private EmailSmtpFacade emailSmtpFacade;

  @Operation(
      operationId = "updateEmailSmtpConfig",
      summary = "更新SMTP配置",
      description = "更新SMTP服务器配置。注意：SSL和STARTTLS是互斥的，不能同时启用。如果两者都为true，将优先使用SSL"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "参数验证失败：SSL和STARTTLS不能同时启用")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping
  public ApiLocaleResult<EmailSmtpVo> updateSmtpConfig(
      @Valid @RequestBody EmailSmtpUpdateDto dto) {
    return ApiLocaleResult.success(emailSmtpFacade.updateSmtpConfig(dto));
  }

  @Operation(
      operationId = "getEmailSmtpConfig",
      summary = "获取SMTP配置",
      description = "获取当前SMTP服务器配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping
  public ApiLocaleResult<EmailSmtpVo> getSmtpConfig() {
    return ApiLocaleResult.success(emailSmtpFacade.getSmtpConfig());
  }

  @Operation(
      operationId = "testEmailSmtpConnection",
      summary = "测试SMTP连接",
      description = "测试SMTP服务器连接。注意：SSL和STARTTLS是互斥的，不能同时启用。如果两者都为true，将优先使用SSL"
  )
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "测试完成"),
      @ApiResponse(responseCode = "400", description = "参数验证失败：SSL和STARTTLS不能同时启用")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/test")
  public ApiLocaleResult<EmailSmtpTestVo> testSmtpConnection(
      @Valid @RequestBody EmailSmtpTestDto dto) {
    return ApiLocaleResult.success(emailSmtpFacade.testSmtpConnection(dto));
  }
}
