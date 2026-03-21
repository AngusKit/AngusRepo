package cloud.xcan.angus.core.gm.interfaces.system.facade.internal;

import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemLicenseFacade;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.LicenseUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.LicenseVo;
import org.springframework.stereotype.Component;

@Component
public class SystemLicenseFacadeImpl implements SystemLicenseFacade {

  @Override
  public LicenseVo updateLicense(LicenseUpdateDto dto) {
    return null; // TODO
  }

  @Override
  public LicenseVo getLicense() {
    return null; // TODO
  }

}
