package cloud.xcan.angus.core.repo.interfaces.reposettings.facade;

import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.GlobalSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookActiveDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookCreateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookFindDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.GlobalSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookLogVo;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookTestResultVo;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface RepoSettingsFacade {

  GlobalSettingsVo getSettings();

  GlobalSettingsVo updateSettings(GlobalSettingsUpdateDto dto);

  WebhookVo createWebhook(WebhookCreateDto dto);

  WebhookVo updateWebhook(Long id, WebhookUpdateDto dto);

  void updateWebhookActive(Long id, WebhookActiveDto dto);

  void deleteWebhook(Long id);

  WebhookVo getWebhookById(Long id);

  PageResult<WebhookVo> listWebhooks(WebhookFindDto dto);

  WebhookTestResultVo testWebhook(Long id);

  List<WebhookLogVo> getWebhookLogs(Long id);
}
