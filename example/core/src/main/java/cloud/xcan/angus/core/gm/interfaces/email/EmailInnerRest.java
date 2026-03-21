package cloud.xcan.angus.core.gm.interfaces.email;

import cloud.xcan.angus.api.gm.email.dto.EmailSendBatchDto;
import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.vo.EmailSendBatchVo;
import cloud.xcan.angus.api.gm.email.vo.EmailSendVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailFacade;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "EmailInner", description = "邮件发送 - 服务内部发送邮件接口")
@Validated
@RestController
@RequestMapping("/innerapi/v1/email")
public class EmailInnerRest {

  @Resource
  private EmailFacade emailFacade;

  @Operation(operationId = "sendEmail", summary = "发送单封邮件", description = "使用模板发送一封邮件")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/send")
  public ApiLocaleResult<EmailSendVo> send(@Valid @RequestBody EmailSendDto dto) {
    return ApiLocaleResult.success(emailFacade.send(dto));
  }

  @Operation(operationId = "sendEmailBatch", summary = "批量发送邮件", description = "批量发送邮件消息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "批量发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/send-batch")
  public ApiLocaleResult<EmailSendBatchVo> sendBatch(@Valid @RequestBody EmailSendBatchDto dto) {
    return ApiLocaleResult.success(emailFacade.sendBatch(dto));
  }

}
