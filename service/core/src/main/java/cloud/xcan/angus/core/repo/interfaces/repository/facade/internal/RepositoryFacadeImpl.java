package cloud.xcan.angus.core.repo.interfaces.repository.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.repo.interfaces.repository.facade.internal.assembler.RepositoryAssembler.getSpecification;
import static cloud.xcan.angus.core.repo.interfaces.repository.facade.internal.assembler.RepositoryAssembler.toCreateEntity;
import static cloud.xcan.angus.core.repo.interfaces.repository.facade.internal.assembler.RepositoryAssembler.toDetailVo;
import static cloud.xcan.angus.core.repo.interfaces.repository.facade.internal.assembler.RepositoryAssembler.toUpdateEntity;
import static cloud.xcan.angus.core.repo.interfaces.repository.facade.internal.assembler.RepositoryAssembler.toUrlVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.repo.application.cmd.repository.RepositoryCmd;
import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.RepositoryFacade;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryCreateDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryFindDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryStatusUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.dto.RepositoryUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryDetailVo;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.vo.RepositoryUrlVo;
import cloud.xcan.angus.core.repo.interfaces.repository.facade.internal.assembler.RepositoryAssembler;
import cloud.xcan.angus.remote.NameJoin;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class RepositoryFacadeImpl implements RepositoryFacade {

  @Resource
  private RepositoryCmd repositoryCmd;

  @Resource
  private RepositoryQuery repositoryQuery;

  @Override
  @NameJoin
  public RepositoryDetailVo create(RepositoryCreateDto dto) {
    RepoEntity entity = toCreateEntity(dto);
    RepoEntity created = repositoryCmd.create(entity);
    return toDetailVo(created);
  }

  @Override
  @NameJoin
  public RepositoryDetailVo update(Long id, RepositoryUpdateDto dto) {
    RepoEntity entity = toUpdateEntity(dto, id);
    RepoEntity updated = repositoryCmd.update(entity);
    return toDetailVo(updated);
  }

  @Override
  @NameJoin
  public RepositoryDetailVo updateStatus(Long id, RepositoryStatusUpdateDto dto) {
    RepoEntity updated = repositoryCmd.updateStatus(id, dto.getStatus());
    return toDetailVo(updated);
  }

  @Override
  public void delete(Long id) {
    repositoryCmd.delete(id);
  }

  @Override
  public void deleteBatch(RepositoryBatchDeleteDto dto) {
    repositoryCmd.deleteBatch(dto.getIds());
  }

  @Override
  @NameJoin
  public RepositoryDetailVo getById(Long id) {
    RepoEntity entity = repositoryQuery.findAndCheck(id);
    return toDetailVo(entity);
  }

  @Override
  @NameJoin
  public PageResult<RepositoryDetailVo> list(RepositoryFindDto dto) {
    Page<RepoEntity> page = repositoryQuery.find(
        getSpecification(dto),
        dto.tranPage(),
        dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, RepositoryAssembler::toDetailVo);
  }

  @Override
  public RepositoryStatisticsVo getStatistics() {
    return repositoryQuery.getStatistics();
  }

  @Override
  public RepositoryUrlVo getUrl(Long id) {
    RepoEntity entity = repositoryQuery.findAndCheck(id);
    return toUrlVo(entity);
  }
}
