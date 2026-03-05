package cloud.xcan.angus.core.repo.interfaces.cleanup.facade;

import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyCreateDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyFindDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.dto.CleanupPolicyUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupExecutionVo;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupPolicyDetailVo;
import cloud.xcan.angus.core.repo.interfaces.cleanup.facade.vo.CleanupStatisticsVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface CleanupFacade {

  CleanupPolicyDetailVo create(CleanupPolicyCreateDto dto);

  CleanupPolicyDetailVo update(String id, CleanupPolicyUpdateDto dto);

  void delete(String id);

  void deleteBatch(CleanupPolicyBatchDeleteDto dto);

  void updateEnabled(String id, Boolean enabled);

  CleanupPolicyDetailVo getById(String id);

  PageResult<CleanupPolicyDetailVo> list(CleanupPolicyFindDto dto);

  CleanupStatisticsVo getStatistics();

  CleanupExecutionVo execute(String policyId);

  void cancelExecution(String executionId);

  List<CleanupExecutionVo> getExecutions(String policyId);
}
