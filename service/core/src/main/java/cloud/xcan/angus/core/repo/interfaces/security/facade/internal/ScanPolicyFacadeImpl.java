package cloud.xcan.angus.core.repo.interfaces.security.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler.ScanPolicyAssembler.getSpecification;
import static cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler.ScanPolicyAssembler.toCreateEntity;
import static cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler.ScanPolicyAssembler.toDetailVo;
import static cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler.ScanPolicyAssembler.toUpdateEntity;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.repo.application.cmd.security.ScanPolicyCmd;
import cloud.xcan.angus.core.repo.application.query.security.ScanPolicyQuery;
import cloud.xcan.angus.core.repo.domain.security.ScanPolicy;
import cloud.xcan.angus.core.repo.interfaces.security.facade.ScanPolicyFacade;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyCreateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyFindDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.dto.ScanPolicyUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.security.facade.internal.assembler.ScanPolicyAssembler;
import cloud.xcan.angus.core.repo.interfaces.security.facade.vo.ScanPolicyDetailVo;
import cloud.xcan.angus.remote.NameJoin;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class ScanPolicyFacadeImpl implements ScanPolicyFacade {

  @Resource
  private ScanPolicyCmd scanPolicyCmd;

  @Resource
  private ScanPolicyQuery scanPolicyQuery;

  @Override
  @NameJoin
  public ScanPolicyDetailVo create(ScanPolicyCreateDto dto) {
    ScanPolicy entity = toCreateEntity(dto);
    ScanPolicy created = scanPolicyCmd.create(entity);
    return toDetailVo(created);
  }

  @Override
  @NameJoin
  public ScanPolicyDetailVo update(String id, ScanPolicyUpdateDto dto) {
    ScanPolicy entity = toUpdateEntity(dto, id);
    ScanPolicy updated = scanPolicyCmd.update(entity);
    return toDetailVo(updated);
  }

  @Override
  public void delete(String id) {
    scanPolicyCmd.delete(id);
  }

  @Override
  public void updateEnabled(String id, Boolean enabled) {
    scanPolicyCmd.updateEnabled(id, enabled, PrincipalContext.getUserId());
  }

  @Override
  @NameJoin
  public ScanPolicyDetailVo getById(String id) {
    ScanPolicy entity = scanPolicyQuery.findAndCheck(id);
    return toDetailVo(entity);
  }

  @Override
  @NameJoin
  public PageResult<ScanPolicyDetailVo> list(ScanPolicyFindDto dto) {
    Page<ScanPolicy> page = scanPolicyQuery.find(
        getSpecification(dto),
        dto.tranPage(),
        dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, ScanPolicyAssembler::toDetailVo);
  }
}
