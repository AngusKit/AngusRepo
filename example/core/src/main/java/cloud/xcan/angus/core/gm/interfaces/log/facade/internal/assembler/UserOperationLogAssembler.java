package cloud.xcan.angus.core.gm.interfaces.log.facade.internal.assembler;

import cloud.xcan.angus.core.gm.domain.log.UserOperationLog;
import cloud.xcan.angus.core.gm.interfaces.log.facade.dto.UserOperationLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.log.facade.vo.UserOperationLogDetailVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

/**
 * 用户操作日志数据组装器
 */
public class UserOperationLogAssembler {

  public static UserOperationLogDetailVo toDetailVo(UserOperationLog log) {
    UserOperationLogDetailVo vo = new UserOperationLogDetailVo();
    vo.setId(log.getId());
    vo.setUserId(log.getUserId());
    vo.setUserName(log.getUserName());
    vo.setAction(log.getAction());
    vo.setResource(log.getResource());
    vo.setResourceType(log.getResourceType());
    vo.setResourceId(log.getResourceId());
    vo.setIp(log.getIp());
    vo.setUserAgent(log.getUserAgent());
    vo.setDetails(log.getDetails());
    vo.setResponseStatus(log.getResponseStatus());
    vo.setErrorMessage(log.getErrorMessage());

    // 设置审计信息
    vo.setTenantId(log.getTenantId());
    vo.setCreatedDate(log.getCreatedDate());
    return vo;
  }

  public static GenericSpecification<UserOperationLog> getSpecification(
      UserOperationLogFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "userId", "createdDate")
        .matchSearchFields("userName", "resource", "details")
        .orderByFields("id", "createdDate", "userId", "action", "resourceType")
        .build();
    return new GenericSpecification<>(filters);
  }
}
