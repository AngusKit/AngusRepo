package cloud.xcan.angus.core.repo.interfaces.access.facade;

import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleCreateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleFindDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessTokenCreateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.PermissionCheckDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessRuleVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessTokenVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.PermissionCheckResultVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.UserPermissionVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface AccessFacade {

  AccessRuleVo createRule(Long repositoryId, AccessRuleCreateDto dto);

  AccessRuleVo updateRule(Long repositoryId, Long id, AccessRuleUpdateDto dto);

  void deleteRule(Long repositoryId, Long id);

  AccessRuleVo getRuleById(Long repositoryId, Long id);

  PageResult<AccessRuleVo> listRules(Long repositoryId, AccessRuleFindDto dto);

  AccessTokenVo createToken(Long repositoryId, AccessTokenCreateDto dto);

  void revokeToken(Long repositoryId, Long id);

  List<AccessTokenVo> listTokens(Long repositoryId);

  PermissionCheckResultVo checkPermission(Long repositoryId, PermissionCheckDto dto);

  UserPermissionVo getUserPermissions(Long repositoryId);

  AccessStatisticsVo getAccessStatistics(Long repositoryId);
}
