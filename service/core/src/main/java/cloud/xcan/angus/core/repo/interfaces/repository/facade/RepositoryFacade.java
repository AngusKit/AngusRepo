package cloud.xcan.angus.core.repo.interfaces.repository.facade;

import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryCreateDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryFindDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryStatusUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryDetailVo;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryUrlVo;
import cloud.xcan.angus.remote.PageResult;

public interface RepositoryFacade {

  RepositoryDetailVo create(RepositoryCreateDto dto);

  RepositoryDetailVo update(Long id, RepositoryUpdateDto dto);

  RepositoryDetailVo updateStatus(Long id, RepositoryStatusUpdateDto dto);

  void delete(Long id);

  void deleteBatch(RepositoryBatchDeleteDto dto);

  RepositoryDetailVo getById(Long id);

  PageResult<RepositoryDetailVo> list(RepositoryFindDto dto);

  RepositoryStatisticsVo getStatistics();

  RepositoryUrlVo getUrl(Long id);
}
