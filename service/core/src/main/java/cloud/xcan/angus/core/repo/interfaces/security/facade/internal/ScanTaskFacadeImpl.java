package cloud.xcan.angus.core.repo.interfaces.security.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler.ScanTaskAssembler.getSpecification;
import static cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler.ScanTaskAssembler.toCreateEntity;
import static cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler.ScanTaskAssembler.toDetailVo;
import static cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler.ScanTaskAssembler.toUpdateEntity;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.repo.application.cmd.security.ScanTaskCmd;
import cloud.xcan.angus.core.repo.application.query.security.ScanTaskQuery;
import cloud.xcan.angus.core.repo.domain.security.ScanTask;
import cloud.xcan.angus.core.repo.interfaces.security.facade.ScanTaskFacade;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskCreateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskFindDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanTaskUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler.ScanTaskAssembler;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanTaskDetailVo;
import cloud.xcan.angus.remote.NameJoin;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ScanTaskFacadeImpl implements ScanTaskFacade {

  @Resource
  private ScanTaskCmd scanTaskCmd;

  @Resource
  private ScanTaskQuery scanTaskQuery;

  @Override
  @NameJoin
  public ScanTaskDetailVo create(ScanTaskCreateDto dto) {
    ScanTask entity = toCreateEntity(dto);
    ScanTask created = scanTaskCmd.create(entity);
    return toDetailVo(created);
  }

  @Override
  @NameJoin
  public ScanTaskDetailVo update(String id, ScanTaskUpdateDto dto) {
    ScanTask entity = toUpdateEntity(dto, id);
    ScanTask updated = scanTaskCmd.update(entity);
    return toDetailVo(updated);
  }

  @Override
  public void cancel(String id) {
    scanTaskCmd.cancel(id);
  }

  @Override
  public void delete(String id) {
    scanTaskCmd.delete(id);
  }

  @Override
  @NameJoin
  public ScanTaskDetailVo getById(String id) {
    ScanTask entity = scanTaskQuery.findAndCheck(id);
    return toDetailVo(entity);
  }

  @Override
  @NameJoin
  public PageResult<ScanTaskDetailVo> list(ScanTaskFindDto dto) {
    Page<ScanTask> page = scanTaskQuery.find(
        getSpecification(dto),
        dto.tranPage(),
        dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, ScanTaskAssembler::toDetailVo);
  }

  @Override
  public ScanStatisticsVo getStatistics() {
    return scanTaskQuery.getStatistics();
  }
}
