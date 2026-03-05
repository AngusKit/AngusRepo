package cloud.xcan.angus.core.repo.interfaces.access.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.repo.interfaces.access.facade.internal.assembler.AccessRuleAssembler.getSpecification;
import static cloud.xcan.angus.core.repo.interfaces.access.facade.internal.assembler.AccessRuleAssembler.toCreateEntity;
import static cloud.xcan.angus.core.repo.interfaces.access.facade.internal.assembler.AccessRuleAssembler.toUpdateEntity;
import static cloud.xcan.angus.core.repo.interfaces.access.facade.internal.assembler.AccessRuleAssembler.toVo;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.repo.application.cmd.access.AccessRuleCmd;
import cloud.xcan.angus.core.repo.application.cmd.access.AccessTokenCmd;
import cloud.xcan.angus.core.repo.application.query.access.AccessQuery;
import cloud.xcan.angus.core.repo.domain.access.AccessRule;
import cloud.xcan.angus.core.repo.domain.access.AccessToken;
import cloud.xcan.angus.core.repo.interfaces.access.facade.AccessFacade;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleCreateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleFindDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessRuleUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.AccessTokenCreateDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.dto.PermissionCheckDto;
import cloud.xcan.angus.core.repo.interfaces.access.facade.internal.assembler.AccessRuleAssembler;
import cloud.xcan.angus.core.repo.interfaces.access.facade.internal.assembler.AccessTokenAssembler;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessRuleVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.AccessTokenVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.PermissionCheckResultVo;
import cloud.xcan.angus.core.repo.interfaces.access.facade.vo.UserPermissionVo;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AccessFacadeImpl implements AccessFacade {

  @Resource
  private AccessRuleCmd accessRuleCmd;

  @Resource
  private AccessTokenCmd accessTokenCmd;

  @Resource
  private AccessQuery accessQuery;

  @Override
  public AccessRuleVo createRule(Long repositoryId, AccessRuleCreateDto dto) {
    AccessRule entity = toCreateEntity(dto, repositoryId);
    AccessRule created = accessRuleCmd.create(entity);
    return toVo(created);
  }

  @Override
  public AccessRuleVo updateRule(Long repositoryId, Long id, AccessRuleUpdateDto dto) {
    AccessRule entity = toUpdateEntity(dto, id);
    AccessRule updated = accessRuleCmd.update(entity);
    return toVo(updated);
  }

  @Override
  public void deleteRule(Long repositoryId, Long id) {
    accessRuleCmd.delete(id);
  }

  @Override
  public AccessRuleVo getRuleById(Long repositoryId, Long id) {
    AccessRule entity = accessQuery.findRuleAndCheck(id);
    return toVo(entity);
  }

  @Override
  public PageResult<AccessRuleVo> listRules(Long repositoryId, AccessRuleFindDto dto) {
    dto.setRepositoryId(repositoryId);
    Page<AccessRule> page = accessQuery.findRules(
        getSpecification(dto),
        dto.tranPage(),
        dto.fullTextSearch,
        getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, AccessRuleAssembler::toVo);
  }

  @Override
  public AccessTokenVo createToken(Long repositoryId, AccessTokenCreateDto dto) {
    AccessToken entity = AccessTokenAssembler.toCreateEntity(dto, repositoryId);
    AccessToken created = accessTokenCmd.create(entity);
    return AccessTokenAssembler.toCreatedVo(created);
  }

  @Override
  public void revokeToken(Long repositoryId, Long id) {
    accessTokenCmd.revoke(id);
  }

  @Override
  public List<AccessTokenVo> listTokens(Long repositoryId) {
    List<AccessToken> tokens = accessQuery.findTokensByRepositoryId(repositoryId);
    return tokens.stream().map(AccessTokenAssembler::toVo).toList();
  }

  @Override
  public PermissionCheckResultVo checkPermission(Long repositoryId, PermissionCheckDto dto) {
    Long userId = PrincipalContext.getUserId();
    boolean allowed = accessQuery.checkPermission(repositoryId, userId,
        dto.getPermission(), dto.getPath());
    PermissionCheckResultVo result = new PermissionCheckResultVo();
    result.setAllowed(allowed);
    result.setReason(allowed ? "Permission granted" : "Permission denied");
    return result;
  }

  @Override
  public UserPermissionVo getUserPermissions(Long repositoryId) {
    Long userId = PrincipalContext.getUserId();
    return accessQuery.getUserPermissions(repositoryId, userId);
  }

  @Override
  public AccessStatisticsVo getAccessStatistics(Long repositoryId) {
    return accessQuery.getAccessStatistics(repositoryId);
  }
}
