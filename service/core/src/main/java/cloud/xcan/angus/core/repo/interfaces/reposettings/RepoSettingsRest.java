package cloud.xcan.angus.core.repo.interfaces.reposettings;

import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.RepoSettingsFacade;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.GlobalSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookActiveDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookCreateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookFindDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.GlobalSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookLogVo;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookTestResultVo;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import cloud.xcan.angus.remote.PageResult;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "RepositorySettings", description = "仓库设置管理 - 全局设置和Webhook管理")
@Validated
@RestController
@RequestMapping("/api/v1/repository-settings")
public class RepoSettingsRest {

  @Resource
  private RepoSettingsFacade repoSettingsFacade;

  @Operation(summary = "获取全局设置", description = "获取仓库全局配置信息",
      operationId = "repoSettings:getSettings")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping
  public ApiLocaleResult<GlobalSettingsVo> getSettings() {
    return ApiLocaleResult.success(repoSettingsFacade.getSettings());
  }

  @Operation(summary = "更新全局设置", description = "更新仓库全局配置信息",
      operationId = "repoSettings:updateSettings")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping
  public ApiLocaleResult<GlobalSettingsVo> updateSettings(
      @Valid @RequestBody GlobalSettingsUpdateDto dto) {
    return ApiLocaleResult.success(repoSettingsFacade.updateSettings(dto));
  }

  @Operation(summary = "创建Webhook", description = "创建新的Webhook",
      operationId = "repoSettings:createWebhook")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "创建成功")
  })
  @PostMapping("/webhooks")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiLocaleResult<WebhookVo> createWebhook(
      @Valid @RequestBody WebhookCreateDto dto) {
    return ApiLocaleResult.success(repoSettingsFacade.createWebhook(dto));
  }

  @Operation(summary = "更新Webhook", description = "更新Webhook基本信息",
      operationId = "repoSettings:updateWebhook")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/webhooks/{id}")
  public ApiLocaleResult<WebhookVo> updateWebhook(
      @PathVariable Long id, @Valid @RequestBody WebhookUpdateDto dto) {
    return ApiLocaleResult.success(repoSettingsFacade.updateWebhook(id, dto));
  }

  @Operation(summary = "启用/禁用Webhook", description = "更新Webhook启用状态",
      operationId = "repoSettings:updateWebhookActive")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "更新成功")
  })
  @PutMapping("/webhooks/{id}/active")
  public ApiLocaleResult<?> updateWebhookActive(
      @PathVariable Long id, @Valid @RequestBody WebhookActiveDto dto) {
    repoSettingsFacade.updateWebhookActive(id, dto);
    return ApiLocaleResult.success();
  }

  @Operation(summary = "删除Webhook", description = "删除指定Webhook",
      operationId = "repoSettings:deleteWebhook")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "删除成功")
  })
  @DeleteMapping("/webhooks/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteWebhook(@PathVariable Long id) {
    repoSettingsFacade.deleteWebhook(id);
  }

  @Operation(summary = "获取Webhook详情", description = "获取Webhook详细信息",
      operationId = "repoSettings:getWebhookById")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功"),
      @ApiResponse(responseCode = "404", description = "Webhook不存在")
  })
  @GetMapping("/webhooks/{id}")
  public ApiLocaleResult<WebhookVo> getWebhookById(@PathVariable Long id) {
    return ApiLocaleResult.success(repoSettingsFacade.getWebhookById(id));
  }

  @Operation(summary = "查询Webhook列表", description = "分页查询Webhook列表",
      operationId = "repoSettings:listWebhooks")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/webhooks")
  public ApiLocaleResult<PageResult<WebhookVo>> listWebhooks(
      @Valid @ParameterObject WebhookFindDto dto) {
    return ApiLocaleResult.success(repoSettingsFacade.listWebhooks(dto));
  }

  @Operation(summary = "测试Webhook", description = "发送测试请求到Webhook",
      operationId = "repoSettings:testWebhook")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "测试完成")
  })
  @PostMapping("/webhooks/{id}/test")
  public ApiLocaleResult<WebhookTestResultVo> testWebhook(@PathVariable Long id) {
    return ApiLocaleResult.success(repoSettingsFacade.testWebhook(id));
  }

  @Operation(summary = "获取Webhook日志", description = "获取Webhook触发日志",
      operationId = "repoSettings:getWebhookLogs")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @GetMapping("/webhooks/{id}/logs")
  public ApiLocaleResult<List<WebhookLogVo>> getWebhookLogs(@PathVariable Long id) {
    return ApiLocaleResult.success(repoSettingsFacade.getWebhookLogs(id));
  }
}
