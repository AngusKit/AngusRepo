package cloud.xcan.angus.core.repo.interfaces.system.facade;

import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.AuthSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.ConnectionTestDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.GeneralSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.IntegrationSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.LicenseUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.StorageSettingsUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.dto.SystemRestartDto;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.AuthSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.ConnectionTestResultVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.GeneralSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.IntegrationSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.LicenseInfoVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.StorageSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.SystemRestartResultVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.SystemSettingsVo;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.SystemStatusVo;

public interface SystemSettingsFacade {

  SystemSettingsVo getSettings();

  GeneralSettingsVo updateGeneralSettings(GeneralSettingsUpdateDto dto);

  StorageSettingsVo updateStorageSettings(StorageSettingsUpdateDto dto);

  AuthSettingsVo updateAuthSettings(AuthSettingsUpdateDto dto);

  IntegrationSettingsVo updateIntegrationSettings(IntegrationSettingsUpdateDto dto);

  ConnectionTestResultVo testConnection(ConnectionTestDto dto);

  SystemStatusVo getSystemStatus();

  LicenseInfoVo getLicense();

  LicenseInfoVo updateLicense(LicenseUpdateDto dto);

  SystemRestartResultVo restart(SystemRestartDto dto);
}
