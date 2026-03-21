package cloud.xcan.angus.core.gm.interfaces.authorization.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.authorization.AuthorizationCmd;
import cloud.xcan.angus.core.gm.application.query.authorization.AuthorizationQuery;
import cloud.xcan.angus.core.gm.domain.authorization.Authorization;
import cloud.xcan.angus.core.gm.domain.authorization.enums.AuthorizationSubjectType;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.AuthorizationFacade;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationBatchCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationBatchDeleteDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationCreateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationFindDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationRoleAddDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.dto.AuthorizationUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.internal.assembler.AuthorizationAssembler;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationBatchVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationDetailVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationListVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationRoleVo;
import cloud.xcan.angus.core.gm.interfaces.authorization.facade.vo.AuthorizationStatsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationFacadeImpl implements AuthorizationFacade {

  @Resource
  private AuthorizationCmd authorizationCmd;

  @Resource
  private AuthorizationQuery authorizationQuery;

  @NameJoin
  @Override
  public AuthorizationDetailVo create(AuthorizationCreateDto dto) {
    Authorization authorization = AuthorizationAssembler.toCreateDomain(dto);
    Authorization saved = authorizationCmd.create(authorization);
    authorizationQuery.setRoleInfo(List.of(saved));
    authorizationQuery.setSubjectUserCounts(List.of(saved));
    return AuthorizationAssembler.toDetailVo(saved);
  }

  @Override
  public AuthorizationBatchVo batchCreate(AuthorizationBatchCreateDto dto) {
    AuthorizationBatchVo resultVo = new AuthorizationBatchVo();
    resultVo.setSubjectType(dto.getSubjectType());
    resultVo.setSubjectCount(dto.getSubjectIds().size());
    resultVo.setRoleCount(dto.getRoleIds().size());

    int successCount = 0;
    for (Long subjectId : dto.getSubjectIds()) {
      try {
        Authorization authorization = AuthorizationAssembler.toCreateDomain(
            dto.getSubjectType(), subjectId, dto.getRoleIds(), dto.getDescription());
        authorizationCmd.create(authorization);
        successCount++;
      } catch (Exception ignored) {
      }
    }
    resultVo.setSuccessCount(successCount);
    return resultVo;
  }

  @NameJoin
  @Override
  public AuthorizationDetailVo update(Long id, AuthorizationUpdateDto dto) {
    Authorization authorization = AuthorizationAssembler.toUpdateDomain(id, dto);
    Authorization saved = authorizationCmd.update(authorization);
    authorizationQuery.setRoleInfo(List.of(saved));
    authorizationQuery.setSubjectUserCounts(List.of(saved));
    return AuthorizationAssembler.toDetailVo(saved);
  }

  @Override
  public AuthorizationRoleVo addRoles(Long id, AuthorizationRoleAddDto dto) {
    Authorization authorization = authorizationCmd.addRoles(id, dto.getRoleIds());
    authorizationQuery.setRoleInfo(List.of(authorization));
    authorizationQuery.setSubjectUserCounts(List.of(authorization));
    return AuthorizationAssembler.toAuthorizationRoleAddVo(id, authorization);
  }

  @Override
  public AuthorizationRoleVo removeRole(Long id, Long roleId) {
    Authorization authorization = authorizationCmd.removeRole(id, roleId);
    authorizationQuery.setRoleInfo(List.of(authorization));
    authorizationQuery.setSubjectUserCounts(List.of(authorization));
    return AuthorizationAssembler.toAuthorizationRoleAddVo(id, authorization);
  }

  @Override
  public AuthorizationRoleVo addRolesToAuthorizationBySubject(
      AuthorizationSubjectType subjectType, Long subjectId, AuthorizationRoleAddDto dto) {
    // 先查找是否已存在授权
    Authorization authorization = authorizationQuery.getSubjectAuthorization(subjectType,
        subjectId);

    if (authorization == null) {
      // 如果授权不存在，先创建授权（不包含角色）
      Authorization newAuthorization = AuthorizationAssembler.toCreateDomain(
          subjectType, subjectId, null, null);
      authorization = authorizationCmd.create(newAuthorization);
    }

    // 添加角色到授权
    Long authorizationId = authorization.getId();
    authorization = authorizationCmd.addRoles(authorizationId, dto.getRoleIds());
    authorizationQuery.setRoleInfo(List.of(authorization));
    authorizationQuery.setSubjectUserCounts(List.of(authorization));
    return AuthorizationAssembler.toAuthorizationRoleAddVo(authorizationId, authorization);
  }

  @Override
  public AuthorizationRoleVo removeRoleFromAuthorizationBySubject(
      AuthorizationSubjectType subjectType, Long subjectId, Long roleId) {
    // 查找授权
    Authorization authorization = authorizationQuery.getSubjectAuthorization(subjectType,
        subjectId);

    if (authorization == null) {
      throw ResourceNotFound.of("授权主体「{0}」的授权记录不存在",
          new Object[]{authorizationQuery.checkSubjectExists(subjectType, subjectId)});
    }

    // 从授权中移除角色
    Long authorizationId = authorization.getId();
    authorization = authorizationCmd.removeRole(authorizationId, roleId);
    authorizationQuery.setRoleInfo(List.of(authorization));
    authorizationQuery.setSubjectUserCounts(List.of(authorization));
    return AuthorizationAssembler.toAuthorizationRoleAddVo(authorizationId, authorization);
  }

  @Override
  public void delete(Long id) {
    authorizationCmd.delete(id);
  }

  @Override
  public void batchDelete(AuthorizationBatchDeleteDto dto) {
    authorizationCmd.batchDelete(dto.getIds());
  }

  @NameJoin
  @Override
  public AuthorizationDetailVo getDetail(Long id) {
    Authorization authorization = authorizationQuery.findAndCheck(id);
    authorizationQuery.setRoleInfo(List.of(authorization));
    authorizationQuery.setSubjectUserCounts(List.of(authorization));
    return AuthorizationAssembler.toDetailVo(authorization);
  }

  @NameJoin
  @Override
  public AuthorizationDetailVo getSubjectAuthorization(
      AuthorizationSubjectType subjectType, Long subjectId) {
    Authorization authorization = authorizationQuery.getSubjectAuthorization(
        subjectType, subjectId);
    if (authorization == null) {
      return null;
    }
    authorizationQuery.setRoleInfo(List.of(authorization));
    authorizationQuery.setSubjectUserCounts(List.of(authorization));
    return AuthorizationAssembler.toDetailVo(authorization);
  }

  @NameJoin
  @Override
  public PageResult<AuthorizationListVo> list(AuthorizationFindDto dto) {
    GenericSpecification<Authorization> spec = AuthorizationAssembler.getSpecification(dto);
    Page<Authorization> page = authorizationQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    authorizationQuery.setRoleInfo(page.getContent());
    authorizationQuery.setSubjectUserCounts(page.getContent());
    return buildVoPageResult(page, AuthorizationAssembler::toListVo);
  }

  @NameJoin
  @Override
  public PageResult<AuthorizationListVo> listUsers(AuthorizationFindDto dto) {
    GenericSpecification<Authorization> spec = AuthorizationAssembler.getSpecificationByType(dto,
        AuthorizationSubjectType.USER);
    Page<Authorization> page = authorizationQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    authorizationQuery.setRoleInfo(page.getContent());
    return buildVoPageResult(page, AuthorizationAssembler::toListVo);
  }

  @NameJoin
  @Override
  public PageResult<AuthorizationListVo> listDepartments(AuthorizationFindDto dto) {
    GenericSpecification<Authorization> spec = AuthorizationAssembler.getSpecificationByType(dto,
        AuthorizationSubjectType.DEPARTMENT);
    Page<Authorization> page = authorizationQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    authorizationQuery.setRoleInfo(page.getContent());
    authorizationQuery.setSubjectUserCounts(page.getContent());
    return buildVoPageResult(page, AuthorizationAssembler::toListVo);
  }

  @NameJoin
  @Override
  public PageResult<AuthorizationListVo> listGroups(AuthorizationFindDto dto) {
    GenericSpecification<Authorization> spec = AuthorizationAssembler.getSpecificationByType(dto,
        AuthorizationSubjectType.GROUP);
    Page<Authorization> page = authorizationQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    authorizationQuery.setRoleInfo(page.getContent());
    authorizationQuery.setSubjectUserCounts(page.getContent());
    return buildVoPageResult(page, AuthorizationAssembler::toListVo);
  }

  @Override
  public AuthorizationStatsVo getStats() {
    return authorizationQuery.getStats();
  }

}
