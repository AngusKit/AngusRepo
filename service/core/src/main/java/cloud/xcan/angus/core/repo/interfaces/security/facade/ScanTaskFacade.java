package cloud.xcan.angus.core.repo.interfaces.security.facade;

import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskCreateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskFindDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanTaskDetailVo;
import cloud.xcan.angus.remote.PageResult;

public interface ScanTaskFacade {
  ScanTaskDetailVo create(ScanTaskCreateDto dto);
  ScanTaskDetailVo update(String id, ScanTaskUpdateDto dto);
  void cancel(String id);
  void delete(String id);
  ScanTaskDetailVo getById(String id);
  PageResult<ScanTaskDetailVo> list(ScanTaskFindDto dto);
  ScanStatisticsVo getStatistics();
}
