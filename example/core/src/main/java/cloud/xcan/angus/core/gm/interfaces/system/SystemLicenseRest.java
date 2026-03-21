package cloud.xcan.angus.core.gm.interfaces.system;

import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemLicenseFacade;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.LicenseUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.LicenseVo;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SystemLicense", description = "系统许可 - 许可安装、验证与管理")
@Validated
@RestController
@RequestMapping("/api/v1/system/license")
public class SystemLicenseRest {

  @Resource
  private SystemLicenseFacade systemLicenseFacade;

  @Operation(operationId = "updateLicense", summary = "更新许可证", description = "更新系统许可证")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "许可证无效")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/license")
  public ApiLocaleResult<LicenseVo> updateLicense(@Valid @RequestBody LicenseUpdateDto dto) {
    return ApiLocaleResult.success(systemLicenseFacade.updateLicense(dto));
  }

  @Operation(operationId = "getLicense", summary = "获取系统许可证信息", description = "获取系统许可证信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/license")
  public ApiLocaleResult<LicenseVo> getLicense() {
    return ApiLocaleResult.success(systemLicenseFacade.getLicense());
  }
}
