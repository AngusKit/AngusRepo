package cloud.xcan.angus.core.gm.interfaces.service;

import cloud.xcan.angus.core.gm.interfaces.service.facade.ServiceConfigFacade;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.dto.EurekaTestDto;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.EurekaConfigVo;
import cloud.xcan.angus.core.gm.interfaces.service.facade.vo.EurekaTestVo;
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

@Tag(name = "ServiceEurekaConfig", description = "服务Eureka配置管理 - 服务注册中心配置与连接检查")
@Validated
@RestController
@RequestMapping("/api/v1/services")
public class ServiceConfigRest {

  @Resource
  private ServiceConfigFacade serviceConfigFacade;

  @Operation(operationId = "updateEurekaConfig", summary = "更新Eureka配置", description = "更新Eureka注册中心配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/eureka/config")
  public ApiLocaleResult<EurekaConfigVo> updateEurekaConfig(
      @Valid @RequestBody EurekaConfigUpdateDto dto) {
    return ApiLocaleResult.success(serviceConfigFacade.updateEurekaConfig(dto));
  }

  @Operation(operationId = "getEurekaConfig", summary = "获取Eureka配置", description = "获取Eureka注册中心配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/eureka/config")
  public ApiLocaleResult<EurekaConfigVo> getEurekaConfig() {
    return ApiLocaleResult.success(serviceConfigFacade.getEurekaConfig());
  }

  @Operation(operationId = "testEurekaConnection", summary = "测试Eureka连接", description = "测试Eureka注册中心连接")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "测试完成")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/eureka/test")
  public ApiLocaleResult<EurekaTestVo> testEurekaConnection(
      @Valid @RequestBody EurekaTestDto dto) {
    return ApiLocaleResult.success(serviceConfigFacade.testEurekaConnection(dto));
  }

}
