package cloud.xcan.angus.core.repo.interfaces.access;

import cloud.xcan.angus.core.repo.interfaces.access.facade.AccessFacade;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleCreateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleFindDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessRuleVo;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AccessRules", description = "访问控制 - 访问规则的创建、更新、删除、查询")
@Validated
@RestController
@RequestMapping("/api/v1/repositories/{repositoryId}/access-rules")
public class AccessRuleRest {

  @Resource
  private AccessFacade accessFacade;

  @Operation(summary = "创建访问规则", description = "为仓库创建新的访问规则",
      operationId = "accessRule:create")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "访问规则创建成功")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<AccessRuleVo> create(
      @PathVariable Long repositoryId,
      @Valid @RequestBody AccessRuleCreateDto dto) {
    return ApiLocaleResult.success(accessFacade.createRule(repositoryId, dto));
  }

  @Operation(summary = "更新访问规则", description = "更新仓库的访问规则",
      operationId = "accessRule:update")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/{id}")
  public ApiLocaleResult<AccessRuleVo> update(
      @PathVariable Long repositoryId,
      @PathVariable Long id,
      @Valid @RequestBody AccessRuleUpdateDto dto) {
    return ApiLocaleResult.success(accessFacade.updateRule(repositoryId, id, dto));
  }

  @Operation(summary = "删除访问规则", description = "删除仓库的访问规则",
      operationId = "accessRule:delete")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @PathVariable Long repositoryId,
      @PathVariable Long id) {
    accessFacade.deleteRule(repositoryId, id);
  }

  @Operation(summary = "查询访问规则详情", description = "获取访问规则详细信息",
      operationId = "accessRule:getById")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "访问规则不存在")
  })
  @GetMapping("/{id}")
  public ApiLocaleResult<AccessRuleVo> getById(
      @PathVariable Long repositoryId,
      @PathVariable Long id) {
    return ApiLocaleResult.success(accessFacade.getRuleById(repositoryId, id));
  }

  @Operation(summary = "查询访问规则列表", description = "分页查询仓库的访问规则列表",
      operationId = "accessRule:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping
  public ApiLocaleResult<PageResult<AccessRuleVo>> list(
      @PathVariable Long repositoryId,
      @Valid @ParameterObject AccessRuleFindDto dto) {
    return ApiLocaleResult.success(accessFacade.listRules(repositoryId, dto));
  }
}
