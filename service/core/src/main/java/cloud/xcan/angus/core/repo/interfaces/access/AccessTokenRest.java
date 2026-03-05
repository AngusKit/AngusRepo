package cloud.xcan.angus.core.repo.interfaces.access;

import cloud.xcan.angus.core.repo.interfaces.access.facade.AccessFacade;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessTokenCreateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessTokenVo;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AccessTokens", description = "访问控制 - 访问令牌的创建、撤销、查询")
@Validated
@RestController
@RequestMapping("/api/v1/repositories/{repositoryId}/tokens")
public class AccessTokenRest {

  @Resource
  private AccessFacade accessFacade;

  @Operation(summary = "创建访问令牌", description = "为仓库创建新的访问令牌",
      operationId = "accessToken:create")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "访问令牌创建成功")
  })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<AccessTokenVo> create(
      @PathVariable Long repositoryId,
      @Valid @RequestBody AccessTokenCreateDto dto) {
    return ApiLocaleResult.success(accessFacade.createToken(repositoryId, dto));
  }

  @Operation(summary = "撤销访问令牌", description = "撤销仓库的访问令牌",
      operationId = "accessToken:revoke")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "撤销成功")
  })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void revoke(
      @PathVariable Long repositoryId,
      @PathVariable Long id) {
    accessFacade.revokeToken(repositoryId, id);
  }

  @Operation(summary = "查询访问令牌列表", description = "查询仓库的访问令牌列表",
      operationId = "accessToken:list")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping
  public ApiLocaleResult<List<AccessTokenVo>> list(
      @PathVariable Long repositoryId) {
    return ApiLocaleResult.success(accessFacade.listTokens(repositoryId));
  }
}
