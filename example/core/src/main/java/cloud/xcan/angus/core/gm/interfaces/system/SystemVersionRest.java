package cloud.xcan.angus.core.gm.interfaces.system;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemVersionFacade;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.ChangelogFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.VersionHistoryFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.ChangelogVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CurrentVersionVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.UpdateCheckVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionCompareVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionDetailVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionHistoryVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SystemVersion", description = "系统版本 - 系统版本信息、更新管理、变更日志")
@Validated
@RestController
@RequestMapping("/api/v1/system/version")
public class SystemVersionRest {

  @Resource
  private SystemVersionFacade systemVersionFacade;

  @Operation(operationId = "getCurrentVersion", summary = "获取当前系统版本信息", description = "获取当前系统版本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/current")
  public ApiLocaleResult<CurrentVersionVo> getCurrentVersion() {
    return ApiLocaleResult.success(systemVersionFacade.getCurrentVersion());
  }

  @Operation(operationId = "getCurrentVersion", summary = "获取指定应用系统版本信息", description = "根据应用编码和版本类型查询指定应用系统版本信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/application")
  public ApiLocaleResult<List<CurrentVersionVo>> getApplicationVersions(
      @Parameter(description = "应用编码", required = true) @RequestParam String appCode,
      @Parameter(description = "版本类型", required = true) @RequestParam EditionType editionType) {
    return ApiLocaleResult.success(
        systemVersionFacade.getApplicationVersions(appCode, editionType));
  }

  @Operation(operationId = "listVersionHistory", summary = "获取版本历史列表", description = "分页获取版本历史列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/history")
  public ApiLocaleResult<PageResult<VersionHistoryVo>> listVersionHistory(
      @Valid @ParameterObject VersionHistoryFindDto dto) {
    return ApiLocaleResult.success(systemVersionFacade.listVersionHistory(dto));
  }

  @Operation(operationId = "getVersionDetail", summary = "获取版本详情", description = "获取指定版本的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "版本不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/history/{id}")
  public ApiLocaleResult<VersionDetailVo> getVersionDetail(
      @Parameter(description = "版本ID") @PathVariable String id) {
    return ApiLocaleResult.success(systemVersionFacade.getVersionDetail(id));
  }

  @Operation(operationId = "getChangelog", summary = "获取变更日志", description = "获取版本变更日志")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/changelog")
  public ApiLocaleResult<PageResult<ChangelogVo>> getChangelog(
      @Valid @ParameterObject ChangelogFindDto dto) {
    return ApiLocaleResult.success(systemVersionFacade.getChangelog(dto));
  }

  @Operation(operationId = "checkUpdate", summary = "检查更新", description = "检查是否有新版本可用")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "检查成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/check-update")
  public ApiLocaleResult<UpdateCheckVo> checkUpdate(
      @Parameter(description = "应用编码", required = true) @RequestParam String appCode,
      @Parameter(description = "版本类型", required = true) @RequestParam EditionType editionType) {
    return ApiLocaleResult.success(systemVersionFacade.checkUpdate(appCode, editionType));
  }

  @Operation(operationId = "compareVersions", summary = "获取版本对比", description = "对比两个版本之间的差异")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "对比成功"),
      @ApiResponse(responseCode = "404", description = "版本不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/compare")
  public ApiLocaleResult<VersionCompareVo> compareVersions(
      @Parameter(description = "应用编码") @RequestParam String appCode,
      @Parameter(description = "起始版本号") @RequestParam String fromVersion,
      @Parameter(description = "目标版本号") @RequestParam String toVersion) {
    return ApiLocaleResult.success(
        systemVersionFacade.compareVersions(appCode, fromVersion, toVersion));
  }

}
