package cloud.xcan.angus.core.repo.interfaces.system.facade.internal.assembler;

import cloud.xcan.angus.core.repo.domain.system.SystemLicense;
import cloud.xcan.angus.core.repo.domain.system.SystemSettings;
import cloud.xcan.angus.core.repo.interfaces.system.facade.vo.LicenseInfoVo;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SystemSettingsAssembler {

  public static Map<String, String> toSettingsMap(List<SystemSettings> settings) {
    return settings.stream()
        .collect(Collectors.toMap(SystemSettings::getSettingKey,
            s -> s.getSettingValue() != null ? s.getSettingValue() : "",
            (v1, v2) -> v2));
  }

  public static LicenseInfoVo toLicenseVo(SystemLicense license) {
    if (license == null) {
      return null;
    }
    LicenseInfoVo vo = new LicenseInfoVo();
    vo.setLicenseType(license.getLicenseType());
    vo.setLicenseTo(license.getLicenseTo());
    vo.setIssuedDate(license.getIssuedDate());
    vo.setExpiresAt(license.getExpiresAt());
    vo.setMaxUsers(license.getMaxUsers());
    vo.setMaxRepositories(license.getMaxRepositories());
    vo.setMaxStorage(license.getMaxStorage());
    vo.setFeatures(license.getFeatures());
    vo.setValid(license.getExpiresAt() != null
        && license.getExpiresAt().isAfter(LocalDateTime.now()));
    return vo;
  }
}
