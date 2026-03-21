package cloud.xcan.angus.api.gm.email;

import cloud.xcan.angus.api.gm.email.dto.EmailSendBatchDto;
import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.vo.EmailSendBatchVo;
import cloud.xcan.angus.api.gm.email.vo.EmailSendVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@FeignClient(name = "${xcan.service.gm:XCAN-ANGUSGM.BOOT}")
public interface EmailRemote {

  @Operation(operationId = "sendEmail", summary = "发送单封邮件", description = "使用模板发送一封邮件")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/api/v1/email/send")
  ApiLocaleResult<EmailSendVo> send(@Valid @RequestBody EmailSendDto dto);

  @Operation(operationId = "sendEmailBatch", summary = "批量发送邮件", description = "批量发送邮件消息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "批量发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/api/v1/email/send-batch")
  ApiLocaleResult<EmailSendBatchVo> sendBatch(@Valid @RequestBody EmailSendBatchDto dto);

}
