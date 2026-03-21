package cloud.xcan.angus.core.gm.interfaces.system.facade;

import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.LicenseUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.LicenseVo;

public interface SystemLicenseFacade {

  /**
   * 更新许可证
   */
  LicenseVo updateLicense(LicenseUpdateDto dto);

  /**
   * 获取系统许可证信息
   */
  LicenseVo getLicense();

}
