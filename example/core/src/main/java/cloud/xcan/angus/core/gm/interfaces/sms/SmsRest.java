package cloud.xcan.angus.core.gm.interfaces.sms;

import cloud.xcan.angus.api.gm.sms.dto.SmsSendBatchDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendBatchVo;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTestDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsRecordVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsStatsVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTestVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SMS", description = "短信消息 - 短信发送、发送记录查询")
@Validated
@RestController
@RequestMapping("/api/v1/sms")
public class SmsRest {

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

  @Operation(operationId = "testSms", summary = "测试短信发送", description = "发送测试短信")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "测试发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/test")
  public ApiLocaleResult<SmsTestVo> test(@Valid @RequestBody SmsTestDto dto) {
    return ApiLocaleResult.success(smsFacade.test(dto));
  }

  @Operation(operationId = "getSmsStats", summary = "获取短信统计数据", description = "获取短信发送统计数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<SmsStatsVo> getStats() {
    return ApiLocaleResult.success(smsFacade.getStats());
  }

  @Operation(operationId = "getSmsRecords", summary = "获取短信记录列表", description = "分页获取短信发送记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/records")
  public ApiLocaleResult<PageResult<SmsRecordVo>> listRecords(
      @ParameterObject SmsRecordFindDto dto) {
    return ApiLocaleResult.success(smsFacade.listRecords(dto));
  }
}
