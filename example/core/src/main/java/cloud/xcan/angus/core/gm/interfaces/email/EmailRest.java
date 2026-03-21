package cloud.xcan.angus.core.gm.interfaces.email;

import cloud.xcan.angus.api.gm.email.dto.EmailSendBatchDto;
import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.vo.EmailSendBatchVo;
import cloud.xcan.angus.api.gm.email.vo.EmailSendVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendCustomDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailRecordVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTrackingVo;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Email", description = "邮件发送 - 邮件发送、发送记录查询、统计数据")
@Validated
@RestController
@RequestMapping("/api/v1/email")
public class EmailRest {

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

  @Operation(operationId = "retryEmail", summary = "重新发送单封邮件", description = "重新使用模板发送一封邮件")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "发送成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/retry")
  public ApiLocaleResult<EmailSendVo> retry(
      @Parameter(description = "邮件ID") @PathVariable Long id) {
    return ApiLocaleResult.success(emailFacade.retry(id));
  }

  @Operation(operationId = "cancelEmail", summary = "取消发送单封邮件", description = "取消发送单封邮件")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "取消成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/cancel")
  public ApiLocaleResult<EmailSendVo> cancel(
      @Parameter(description = "邮件ID") @PathVariable Long id) {
    return ApiLocaleResult.success(emailFacade.cancel(id));
  }

  @Operation(operationId = "sendEmailCustom", summary = "发送自定义邮件", description = "不使用模板发送自定义邮件")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "发送成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/send-custom")
  public ApiLocaleResult<EmailSendVo> sendCustom(@Valid @RequestBody EmailSendCustomDto dto) {
    return ApiLocaleResult.success(emailFacade.sendCustom(dto));
  }

  @Operation(operationId = "getEmailStats", summary = "获取邮件统计数据", description = "获取邮件发送统计数据")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/stats")
  public ApiLocaleResult<EmailStatsVo> getStats() {
    return ApiLocaleResult.success(emailFacade.getStats());
  }

  @Operation(operationId = "getEmailRecords", summary = "获取邮件记录列表", description = "分页获取邮件发送记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/records")
  public ApiLocaleResult<PageResult<EmailRecordVo>> listRecords(
      @Valid @ParameterObject EmailRecordFindDto dto) {
    return ApiLocaleResult.success(emailFacade.listRecords(dto));
  }

  @Operation(operationId = "getEmailTracking", summary = "获取邮件打开/点击统计", description = "获取指定邮件的打开和点击统计信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "邮件记录不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}/stats")
  public ApiLocaleResult<EmailTrackingVo> getEmailTracking(
      @Parameter(description = "邮件记录ID") @PathVariable Long id) {
    return ApiLocaleResult.success(emailFacade.getEmailTracking(id));
  }
}
