package cloud.xcan.angus.core.repo.interfaces.security;

import cloud.xcan.angus.core.repo.interfaces.security.facade.ScanPolicyFacade;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyCreateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyFindDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanPolicyDetailVo;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SecurityPolicies", description = "扫描策略管理 - 策略的创建、更新、启用/禁用")
@Validated
@RestController
@RequestMapping("/api/v1/security/policies")
public class ScanPolicyRest {

  @Resource
  private ScanPolicyFacade scanPolicyFacade;

  @Operation(summary = "创建扫描策略", description = "创建新的扫描策略", operationId = "scanPolicy:create")
  @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "创建成功")})
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<ScanPolicyDetailVo> create(@Valid @RequestBody ScanPolicyCreateDto dto) {
    return ApiLocaleResult.success(scanPolicyFacade.create(dto));
  }

  @Operation(summary = "更新扫描策略", description = "更新扫描策略信息", operationId = "scanPolicy:update")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "更新成功")})
  @PutMapping("/{id}")
  public ApiLocaleResult<ScanPolicyDetailVo> update(@Parameter(name = "id", description = "扫描策略ID") @PathVariable String id, @Valid @RequestBody ScanPolicyUpdateDto dto) {
    return ApiLocaleResult.success(scanPolicyFacade.update(id, dto));
  }

  @Operation(summary = "启用/禁用策略", description = "启用或禁用扫描策略", operationId = "scanPolicy:updateEnabled")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "操作成功")})
  @PutMapping("/{id}/enabled")
  public ApiLocaleResult<?> updateEnabled(@Parameter(name = "id", description = "扫描策略ID") @PathVariable String id, @RequestParam Boolean enabled) {
    scanPolicyFacade.updateEnabled(id, enabled);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "删除扫描策略", description = "删除扫描策略", operationId = "scanPolicy:delete")
  @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "删除成功")})
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@Parameter(name = "id", description = "扫描策略ID") @PathVariable String id) {
    scanPolicyFacade.delete(id);
  }

  @Operation(summary = "查询策略详情", description = "获取扫描策略详细信息", operationId = "scanPolicy:getById")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "策略不存在")
  })
  @GetMapping("/{id}")
  public ApiLocaleResult<ScanPolicyDetailVo> getById(@Parameter(name = "id", description = "扫描策略ID") @PathVariable String id) {
    return ApiLocaleResult.success(scanPolicyFacade.getById(id));
  }

  @Operation(summary = "查询策略列表", description = "分页查询扫描策略列表", operationId = "scanPolicy:list")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功")})
  @GetMapping
  public ApiLocaleResult<PageResult<ScanPolicyDetailVo>> list(@Valid @ParameterObject ScanPolicyFindDto dto) {
    return ApiLocaleResult.success(scanPolicyFacade.list(dto));
  }
}
