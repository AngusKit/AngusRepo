package cloud.xcan.angus.core.gm.interfaces.sms;

import cloud.xcan.angus.api.gm.sms.dto.SmsSendBatchDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendBatchVo;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsFacade;
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

@Tag(name = "SMSInner", description = "短信消息 - 服务内容发送短信接口")
@Validated
@RestController
@RequestMapping("/innerapi/v1/sms")
public class SmsInnerRest {

  @Resource
  private SmsFacade smsFacade;

  @Operation(operationId = "sendSms", summary = "发送单条短信", description = "发送一条短信消息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/send")
  public ApiLocaleResult<SmsSendVo> send(@Valid @RequestBody SmsSendDto dto) {
    return ApiLocaleResult.success(smsFacade.send(dto));
  }

  @Operation(operationId = "sendSmsBatch", summary = "批量发送短信", description = "批量发送短信消息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "批量发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/send-batch")
  public ApiLocaleResult<SmsSendBatchVo> sendBatch(@Valid @RequestBody SmsSendBatchDto dto) {
    return ApiLocaleResult.success(smsFacade.sendBatch(dto));
  }

}
