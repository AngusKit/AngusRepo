package cloud.xcan.angus.api.gm.sms;

import cloud.xcan.angus.api.gm.sms.dto.SmsSendBatchDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendBatchVo;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendVo;
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
public interface SmsRemote {

  @Operation(operationId = "sendSms", summary = "发送单条短信", description = "发送一条短信消息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/api/v1/sms/send")
  ApiLocaleResult<SmsSendVo> send(@Valid @RequestBody SmsSendDto dto);

  @Operation(operationId = "sendSmsBatch", summary = "批量发送短信", description = "批量发送短信消息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "批量发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/api/v1/sms/send-batch")
  ApiLocaleResult<SmsSendBatchVo> sendBatch(@Valid @RequestBody SmsSendBatchDto dto);
}
