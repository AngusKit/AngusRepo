package cloud.xcan.angus.core.repo.interfaces.security.facade;

import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyCreateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyFindDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanPolicyDetailVo;
import cloud.xcan.angus.remote.PageResult;

public interface ScanPolicyFacade {
  ScanPolicyDetailVo create(ScanPolicyCreateDto dto);
  ScanPolicyDetailVo update(String id, ScanPolicyUpdateDto dto);
  void delete(String id);
  void updateEnabled(String id, Boolean enabled);
  ScanPolicyDetailVo getById(String id);
  PageResult<ScanPolicyDetailVo> list(ScanPolicyFindDto dto);
}
