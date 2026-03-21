package cloud.xcan.angus.core.gm.interfaces.interfaces.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.stringSafe;

import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLog;
import cloud.xcan.angus.core.gm.domain.interfaces.InterfaceRequestLogInfo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogCreateDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.dto.InterfaceRequestLogFindDto;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceRequestLogDetailVo;
import cloud.xcan.angus.core.gm.interfaces.interfaces.facade.vo.InterfaceRequestLogListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * API请求日志数据组装器
 */
public class InterfaceRequestLogAssembler {

  public static InterfaceRequestLog toCreateDomain(InterfaceRequestLogCreateDto dto) {
    InterfaceRequestLog log = new InterfaceRequestLog();
    log.setRequestId(dto.getRequestId());
    log.setRemote(dto.getRemote());
    log.setClientId(dto.getClientId());
    log.setClientSource(dto.getClientSource());
    log.setTenantId(dto.getTenantId());
    log.setTenantName(dto.getTenantName());
    log.setUserId(dto.getUserId());
    log.setUserName(dto.getFullName());
    log.setApiKey(dto.getApiKey());
    log.setApiKeyId(dto.getApiKeyId());
    log.setServiceCode(dto.getServiceCode());
    log.setServiceName(stringSafe(dto.getServiceName(), dto.getServiceCode()));
    log.setInstanceId(dto.getInstanceId());
    log.setApiType(dto.getApiType());
    log.setMethod(dto.getMethod());
    log.setUri(dto.getUri());
    log.setRequestDate(dto.getRequestDate());
    log.setQueryParams(dto.getQueryParam());
    log.setRequestHeaders(dto.getRequestHeaders());
    log.setRequestBody(dto.getRequestBody());
    log.setRequestSize(dto.getRequestSize());
    log.setStatus(dto.getStatus());
    log.setResponseHeaders(dto.getResponseHeaders());
    log.setResponseBody(dto.getResponseBody());
    log.setResponseDate(dto.getResponseDate());
    log.setResponseSize(dto.getResponseSize());
    log.setElapsedMillis(dto.getElapsedMillis());
    log.setCreatedDate(LocalDateTime.now());
    return log;
  }

  public static List<InterfaceRequestLog> toCreateDomainList(
      List<InterfaceRequestLogCreateDto> dtos) {
    if (dtos == null || dtos.isEmpty()) {
      return List.of();
    }
    return dtos.stream()
        .map(InterfaceRequestLogAssembler::toCreateDomain)
        .collect(Collectors.toList());
  }

  public static InterfaceRequestLogDetailVo toDetailVo(InterfaceRequestLog log) {
    InterfaceRequestLogDetailVo vo = new InterfaceRequestLogDetailVo();
    vo.setId(log.getId());
    vo.setRequestId(log.getRequestId());
    vo.setRemote(log.getRemote());
    vo.setClientId(log.getClientId());
    vo.setClientSource(log.getClientSource());
    vo.setTenantName(log.getTenantName());
    vo.setUserId(log.getUserId());
    vo.setUserName(log.getUserName());
    vo.setApiKey(log.getApiKey());
    vo.setApiKeyId(log.getApiKeyId());
    vo.setEditionType(log.getEditionType());
    vo.setApplicationCode(log.getApplicationCode());
    vo.setServiceCode(log.getServiceCode());
    vo.setServiceName(log.getServiceName());
    vo.setInstanceId(log.getInstanceId());
    vo.setApiType(log.getApiType());
    vo.setMethod(log.getMethod());
    vo.setUri(log.getUri());
    vo.setRequestDate(log.getRequestDate());
    vo.setQueryParam(log.getQueryParams());
    vo.setRequestHeaders(log.getRequestHeaders());
    vo.setRequestBody(log.getRequestBody());
    vo.setRequestSize(log.getRequestSize());
    vo.setStatus(log.getStatus());
    vo.setResponseHeaders(log.getResponseHeaders());
    vo.setResponseBody(log.getResponseBody());
    vo.setResponseDate(log.getResponseDate());
    vo.setResponseSize(log.getResponseSize());
    vo.setElapsedMillis(log.getElapsedMillis());

    // 设置审计信息
    vo.setTenantId(log.getTenantId());
    vo.setTenantName(log.getTenantName());
    vo.setCreatedDate(log.getCreatedDate());
    return vo;
  }

  public static InterfaceRequestLogListVo toListVo(InterfaceRequestLogInfo log) {
    InterfaceRequestLogListVo vo = new InterfaceRequestLogListVo();
    vo.setId(log.getId());
    vo.setRequestId(log.getRequestId());
    vo.setRemote(log.getRemote());
    vo.setClientId(log.getClientId());
    vo.setClientSource(log.getClientSource());
    vo.setTenantName(log.getTenantName());
    vo.setUserId(log.getUserId());
    vo.setUserName(log.getUserName());
    vo.setApiKey(log.getApiKey());
    vo.setApiKeyId(log.getApiKeyId());
    vo.setEditionType(log.getEditionType());
    vo.setApplicationCode(log.getApplicationCode());
    vo.setServiceCode(log.getServiceCode());
    vo.setServiceName(log.getServiceName());
    vo.setInstanceId(log.getInstanceId());
    vo.setApiType(log.getApiType());
    vo.setMethod(log.getMethod());
    vo.setUri(log.getUri());
    vo.setRequestDate(log.getRequestDate());
    vo.setStatus(log.getStatus());
    vo.setResponseDate(log.getResponseDate());
    vo.setElapsedMillis(log.getElapsedMillis());

    // 设置审计信息
    vo.setTenantId(log.getTenantId());
    vo.setTenantName(log.getTenantName());
    vo.setCreatedDate(log.getCreatedDate());
    return vo;
  }

  public static GenericSpecification<InterfaceRequestLogInfo> getSpecification(
      InterfaceRequestLogFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "status", "elapsedMillis", "requestDate", "responseDate",
            "createdDate")
        .matchSearchFields("uri", "apiKeyId", "serviceCode", "instanceId",
            "requestId", "userName")
        .orderByFields("id", "requestDate", "responseDate", "elapsedMillis",
            "status", "method")
        .build();
    return new GenericSpecification<>(filters);
  }

}
