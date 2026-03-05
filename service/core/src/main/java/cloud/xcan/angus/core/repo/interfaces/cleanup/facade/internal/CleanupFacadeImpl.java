package cloud.xcan.angus.core.repo.interfaces.cleanup.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.repo.interfaces.cleanup.facade.internal.assembler.CleanupAssembler.getSpecification;
import static cloud.xcan.angus.core.repo.interfaces.cleanup.facade.internal.assembler.CleanupAssembler.toCreateEntity;
import static cloud.xcan.angus.core.repo.interfaces.cleanup.facade.internal.assembler.CleanupAssembler.toExecutionVo;
import static cloud.xcan.angus.core.repo.interfaces.cleanup.facade.internal.assembler.CleanupAssembler.toPolicyDetailVo;
import static cloud.xcan.angus.core.repo.interfaces.cleanup.facade.internal.assembler.CleanupAssembler.toUpdateEntity;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.repo.application.cmd.cleanup.CleanupCmd;
import cloud.xcan.angus.core.repo.application.query.cleanup.CleanupQuery;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupExecution;
import cloud.xcan.angus.core.repo.domain.cleanup.CleanupPolicy;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.CleanupFacade;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyCreateDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyFindDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.internal.assembler.CleanupAssembler;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupExecutionVo;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupPolicyDetailVo;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupStatisticsVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class CleanupFacadeImpl implements CleanupFacade {

  @Resource
  private CleanupCmd cleanupCmd;

  @Resource
  private CleanupQuery cleanupQuery;

  @Override
  public CleanupPolicyDetailVo create(CleanupPolicyCreateDto dto) {
    CleanupPolicy entity = toCreateEntity(dto);
    CleanupPolicy created = cleanupCmd.create(entity);
    return toPolicyDetailVo(created);
  }

  @Override
  public CleanupPolicyDetailVo update(String id, CleanupPolicyUpdateDto dto) {
    CleanupPolicy entity = toUpdateEntity(dto, id);
    CleanupPolicy updated = cleanupCmd.update(entity);
    return toPolicyDetailVo(updated);
  }

  @Override
  public void delete(String id) {
    cleanupCmd.delete(id);
  }

  @Override
  public void deleteBatch(CleanupPolicyBatchDeleteDto dto) {
    cleanupCmd.deleteBatch(dto.getIds());
  }

  @Override
  public void updateEnabled(String id, Boolean enabled) {
    cleanupCmd.updateEnabled(id, enabled, PrincipalContext.getUserId());
  }

  @Override
  public CleanupPolicyDetailVo getById(String id) {
    CleanupPolicy entity = cleanupQuery.findPolicyAndCheck(id);
    return toPolicyDetailVo(entity);
  }

  @Override
  public PageResult<CleanupPolicyDetailVo> list(CleanupPolicyFindDto dto) {
    Page<CleanupPolicy> page = cleanupQuery.find(
        getSpecification(dto),
        dto.tranPage(),
        dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, CleanupAssembler::toPolicyDetailVo);
  }

  @Override
  public CleanupStatisticsVo getStatistics() {
    return cleanupQuery.getStatistics();
  }

  @Override
  public CleanupExecutionVo execute(String policyId) {
    CleanupExecution execution = cleanupCmd.execute(policyId);
    return toExecutionVo(execution);
  }

  @Override
  public void cancelExecution(String executionId) {
    cleanupCmd.cancelExecution(executionId);
  }

  @Override
  public List<CleanupExecutionVo> getExecutions(String policyId) {
    List<CleanupExecution> executions = cleanupQuery.findExecutionsByPolicyId(policyId);
    return executions.stream().map(CleanupAssembler::toExecutionVo).toList();
  }
}
