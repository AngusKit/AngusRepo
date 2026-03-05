package cloud.xcan.angus.core.repo.interfaces.reposettings.facade.internal.assembler;

import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.core.repo.domain.reposettings.Webhook;
import cloud.xcan.angus.core.repo.domain.reposettings.WebhookLog;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookCreateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookFindDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.dto.WebhookUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookLogVo;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookTestResultVo;
import cloud.xcan.angus.core.repo.interfaces.reposettings.facade.vo.WebhookVo;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Set;

public class WebhookAssembler {

  public static Webhook toCreateEntity(WebhookCreateDto dto) {
    Webhook entity = new Webhook();
    entity.setName(dto.getName());
    entity.setUrl(dto.getUrl());
    entity.setEvents(dto.getEvents());
    entity.setSecret(dto.getSecret());
    entity.setActive(dto.getActive());
    return entity;
  }

  public static Webhook toUpdateEntity(WebhookUpdateDto dto, Long id) {
    Webhook entity = new Webhook();
    entity.setId(id);
    entity.setName(dto.getName());
    entity.setUrl(dto.getUrl());
    entity.setEvents(dto.getEvents());
    entity.setSecret(dto.getSecret());
    entity.setActive(dto.getActive());
    return entity;
  }

  public static WebhookVo toWebhookVo(Webhook entity) {
    if (entity == null) {
      return null;
    }
    WebhookVo vo = new WebhookVo();
    vo.setId(entity.getId());
    vo.setName(entity.getName());
    vo.setUrl(entity.getUrl());
    vo.setSecret(entity.getSecret());
    vo.setActive(entity.getActive());
    vo.setLastTriggerTime(entity.getLastTriggerTime());
    vo.setSuccessCount(entity.getSuccessCount());
    vo.setFailureCount(entity.getFailureCount());
    vo.setEvents(entity.getEvents());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static WebhookLogVo toWebhookLogVo(WebhookLog entity) {
    if (entity == null) {
      return null;
    }
    WebhookLogVo vo = new WebhookLogVo();
    vo.setId(entity.getId());
    vo.setWebhookId(entity.getWebhookId());
    vo.setEvent(entity.getEvent());
    vo.setStatusCode(entity.getStatusCode());
    vo.setSuccess(entity.getSuccess());
    vo.setRequest(entity.getRequest());
    vo.setResponse(entity.getResponse());
    vo.setResponseTime(entity.getResponseTime());
    vo.setTriggeredAt(entity.getTriggeredAt());
    return vo;
  }

  public static WebhookTestResultVo toTestResultVo(WebhookLog log) {
    WebhookTestResultVo vo = new WebhookTestResultVo();
    vo.setSuccess(log.getSuccess());
    vo.setStatusCode(log.getStatusCode());
    vo.setResponseTime(log.getResponseTime());
    vo.setResponse(log.getResponse());
    return vo;
  }

  public static GenericSpecification<Webhook> getSpecification(WebhookFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .matchSearchFields("name")
        .orderByFields("id", "name", "createdDate", "active")
        .inAndNotFields("active")
        .build();
    return new GenericSpecification<>(filters);
  }
}
