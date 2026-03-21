package cloud.xcan.angus.core.gm.interfaces.ldap;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.LdapFacade;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapAuthTestDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConfigCreateDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConnectionTestDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapSyncHistoryFindDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapUserSearchDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapAuthTestVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapConfigVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapConnectionTestVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapGroupMembersVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapGroupVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapSyncAllResultVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapSyncDetailVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapSyncHistoryVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapSyncResultVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapUserVo;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "LDAP", description = "LDAP集成 - LDAP服务器配置、用户同步、认证集成")
@Validated
@RestController
@RequestMapping("/api/v1/ldap")
public class LdapRest {

  @Resource
  private LdapFacade ldapFacade;

  @Operation(operationId = "createLdapConfig", summary = "创建LDAP配置", description = "创建新的LDAP服务器配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/config")
  public ApiLocaleResult<LdapConfigVo> create(@Valid @RequestBody LdapConfigCreateDto dto) {
    return ApiLocaleResult.success(ldapFacade.create(dto));
  }

  @Operation(operationId = "deleteLdapConfig", summary = "删除LDAP配置", description = "删除LDAP服务器配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/config/{id}")
  public void delete(
      @Parameter(description = "LDAP配置ID") @PathVariable Long id) {
    ldapFacade.delete(id);
  }

  @Operation(operationId = "updateLdapConfig", summary = "更新LDAP配置", description = "更新LDAP服务器配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/config/{id}")
  public ApiLocaleResult<LdapConfigVo> updateConfig(
      @Parameter(description = "LDAP配置ID") @PathVariable Long id,
      @Valid @RequestBody LdapConfigUpdateDto dto) {
    return ApiLocaleResult.success(ldapFacade.updateConfig(id, dto));
  }

  @Operation(operationId = "updateLdapConfigStatus", summary = "启用/禁用LDAP配置", description = "修改LDAP配置启用状态，允许同时启用多个LDAP")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "状态更新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PatchMapping("/config/{id}/status")
  public ApiLocaleResult<LdapConfigVo> updateStatus(
      @Parameter(description = "LDAP配置ID") @PathVariable Long id,
      @Valid @RequestBody EnabledStatusUpdateDto dto) {
    return ApiLocaleResult.success(ldapFacade.updateStatus(id, dto));
  }

  @Operation(operationId = "testLdapConnection", summary = "测试LDAP连接", description = "测试LDAP服务器连接是否正常")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "测试完成"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/test-connection")
  public ApiLocaleResult<LdapConnectionTestVo> testConnection(
      @Valid @RequestBody LdapConnectionTestDto dto) {
    return ApiLocaleResult.success(ldapFacade.testConnection(dto));
  }

  @Operation(operationId = "testLdapAuth", summary = "测试LDAP认证", description = "使用指定用户名密码测试LDAP认证")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "测试完成"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/config/{id}/test-auth")
  public ApiLocaleResult<LdapAuthTestVo> testAuth(
      @Parameter(description = "LDAP配置ID") @PathVariable Long id,
      @Valid @RequestBody LdapAuthTestDto dto) {
    return ApiLocaleResult.success(ldapFacade.testAuth(id, dto));
  }

  @Operation(operationId = "syncLdapUsers", summary = "同步单个LDAP配置用户", description = "手动触发指定LDAP配置的用户同步任务")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "同步任务已启动"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/config/{configId}/sync-users")
  public ApiLocaleResult<LdapSyncResultVo> syncUsers(
      @Parameter(description = "LDAP配置ID") @PathVariable Long configId) {
    return ApiLocaleResult.success(ldapFacade.syncUsers(configId));
  }

  @Operation(operationId = "syncAllLdapUsers", summary = "同步所有LDAP配置用户", description = "手动触发所有已启用LDAP配置的用户同步任务")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "同步任务已启动"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/sync-users")
  public ApiLocaleResult<LdapSyncAllResultVo> syncAllUsers() {
    return ApiLocaleResult.success(ldapFacade.syncAllUsers());
  }

  @Operation(operationId = "searchLdapUsers", summary = "搜索LDAP用户", description = "在LDAP目录中搜索用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "搜索成功"),
      @ApiResponse(responseCode = "400", description = "参数错误")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/search-users")
  public ApiLocaleResult<List<LdapUserVo>> searchUsers(@Valid @RequestBody LdapUserSearchDto dto) {
    return ApiLocaleResult.success(ldapFacade.searchUsers(dto));
  }

  @Operation(operationId = "listLdapConfigs", summary = "获取所有LDAP配置", description = "获取所有LDAP服务器配置列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/config")
  public ApiLocaleResult<List<LdapConfigVo>> listConfigs() {
    return ApiLocaleResult.success(ldapFacade.listConfigs());
  }

  @Operation(operationId = "getLdapSyncHistory", summary = "获取同步历史记录", description = "分页获取LDAP用户同步历史记录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/sync-history")
  public ApiLocaleResult<PageResult<LdapSyncHistoryVo>> listSyncHistory(
      @Valid @ParameterObject LdapSyncHistoryFindDto dto) {
    return ApiLocaleResult.success(ldapFacade.listSyncHistory(dto));
  }

  @Operation(operationId = "getLdapSyncDetail", summary = "获取同步详情", description = "获取指定同步记录的详细信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "记录不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/sync-history/{id}")
  public ApiLocaleResult<LdapSyncDetailVo> getSyncDetail(
      @Parameter(description = "同步记录ID") @PathVariable Long id) {
    return ApiLocaleResult.success(ldapFacade.getSyncDetail(id));
  }

  @Operation(operationId = "getLdapGroups", summary = "获取LDAP组列表", description = "获取LDAP目录中的组列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/groups")
  public ApiLocaleResult<List<LdapGroupVo>> listGroups() {
    return ApiLocaleResult.success(ldapFacade.listGroups());
  }

  @Operation(operationId = "getLdapGroupMembers", summary = "获取LDAP组成员", description = "获取指定LDAP组的成员列表")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功"),
      @ApiResponse(responseCode = "404", description = "组不存在")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/groups/{groupDN}/members")
  public ApiLocaleResult<LdapGroupMembersVo> getGroupMembers(
      @Parameter(description = "组DN（URL编码）") @PathVariable String groupDN) {
    return ApiLocaleResult.success(ldapFacade.getGroupMembers(groupDN));
  }

}
