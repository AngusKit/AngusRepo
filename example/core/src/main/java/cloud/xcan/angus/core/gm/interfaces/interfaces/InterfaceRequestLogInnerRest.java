package cloud.xcan.angus.core.gm.interfaces.interfaces;

import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.InterfaceRequestLogFacade;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogCreateDto;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "InterfaceRequestLogInner", description = "API请求日志收集 - 用于写入接口请求日志记录")
@Validated
@RestController
@RequestMapping("/innerapi/v1/interface/logs")
public class InterfaceRequestLogInnerRest {

  @Resource
  private InterfaceRequestLogFacade interfaceRequestLogFacade;

  @Operation(operationId = "batchCreateInterfaceRequestLog", summary = "批量保存日志记录",
      description = "批量保存API请求日志记录，用于内部服务调用")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "批量保存成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/batch")
  public ApiLocaleResult<?> batchCreate(
      @Valid @RequestBody List<InterfaceRequestLogCreateDto> dto) {
    interfaceRequestLogFacade.batchCreate(dto);
    return ApiLocaleResult.success();
  }
}
