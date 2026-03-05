package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.internal;

import static cloud.xcan.angus.core.repo.interfaces.reposettings.facade.internal.assembler.RepoSettingsAssembler.toSettingsVo;
import static cloud.xcan.angus.core.repo.interfaces.reposettings.facade.internal.assembler.RepoSettingsAssembler.toUpdateEntity;
import static cloud.xcan.angus.core.repo.interfaces.reposettings.facade.internal.assembler.WebhookAssembler.getSpecification;
import static cloud.xcan.angus.core.repo.interfaces.reposettings.facade.internal.assembler.WebhookAssembler.toCreateEntity;
import static cloud.xcan.angus.core.repo.interfaces.reposettings.facade.internal.assembler.WebhookAssembler.toTestResultVo;
import static cloud.xcan.angus.core.repo.interfaces.reposettings.facade.internal.assembler.WebhookAssembler.toWebhookLogVo;
import static cloud.xcan.angus.core.repo.interfaces.reposettings.facade.internal.assembler.WebhookAssembler.toWebhookVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.repo.application.cmd.reposettings.RepoSettingsCmd;
import cloud.xcan.angus.core.repo.application.cmd.reposettings.WebhookCmd;
import cloud.xcan.angus.core.repo.application.query.reposettings.RepoSettingsQuery;
import cloud.xcan.angus.core.repo.domain.reposettings.RepositoryGlobalSettings;
import cloud.xcan.angus.core.repo.domain.reposettings.Webhook;
import cloud.xcan.angus.core.repo.domain.reposettings.WebhookLog;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.RepoSettingsFacade;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.GlobalSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookActiveDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookCreateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookFindDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.internal.assembler.WebhookAssembler;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.GlobalSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookLogVo;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookTestResultVo;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class RepoSettingsFacadeImpl implements RepoSettingsFacade {

  @Resource
  private RepoSettingsCmd repoSettingsCmd;

  @Resource
  private WebhookCmd webhookCmd;

  @Resource
  private RepoSettingsQuery repoSettingsQuery;

  @Override
  public GlobalSettingsVo getSettings() {
    return repoSettingsQuery.getSettings()
        .map(RepoSettingsFacadeImpl::toVo)
        .orElseGet(GlobalSettingsVo::new);
  }

  @Override
  public GlobalSettingsVo updateSettings(GlobalSettingsUpdateDto dto) {
    RepositoryGlobalSettings entity = toUpdateEntity(dto);
    RepositoryGlobalSettings updated = repoSettingsCmd.updateSettings(entity);
    return toSettingsVo(updated);
  }

  @Override
  public WebhookVo createWebhook(WebhookCreateDto dto) {
    Webhook entity = toCreateEntity(dto);
    Webhook created = webhookCmd.create(entity);
    return toWebhookVo(created);
  }

  @Override
  public WebhookVo updateWebhook(Long id, WebhookUpdateDto dto) {
    Webhook entity = WebhookAssembler.toUpdateEntity(dto, id);
    Webhook updated = webhookCmd.update(entity);
    return toWebhookVo(updated);
  }

  @Override
  public void updateWebhookActive(Long id, WebhookActiveDto dto) {
    webhookCmd.updateActive(id, dto.getActive());
  }

  @Override
  public void deleteWebhook(Long id) {
    webhookCmd.delete(id);
  }

  @Override
  public WebhookVo getWebhookById(Long id) {
    Webhook entity = repoSettingsQuery.findWebhookById(id);
    return toWebhookVo(entity);
  }

  @Override
  public PageResult<WebhookVo> listWebhooks(WebhookFindDto dto) {
    Page<Webhook> page = repoSettingsQuery.listWebhooks(
        getSpecification(dto), dto.tranPage());
    return buildVoPageResult(page, WebhookAssembler::toWebhookVo);
  }

  @Override
  public WebhookTestResultVo testWebhook(Long id) {
    Webhook webhook = webhookCmd.test(id);
    return toTestResultVo(webhook);
  }

  @Override
  public List<WebhookLogVo> getWebhookLogs(Long id) {
    List<WebhookLog> logs = repoSettingsQuery.getWebhookLogs(id);
    return logs.stream().map(WebhookAssembler::toWebhookLogVo).toList();
  }

  private static GlobalSettingsVo toVo(RepositoryGlobalSettings entity) {
    return toSettingsVo(entity);
  }
}
