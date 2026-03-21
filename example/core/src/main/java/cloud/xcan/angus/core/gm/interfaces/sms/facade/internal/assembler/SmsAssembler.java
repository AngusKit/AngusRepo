package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.maskDigits;
import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.maskPhone;

import cloud.xcan.angus.api.commonlink.sms.SmsStatus;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendBatchVo;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendVo;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsRecordVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTestVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public class SmsAssembler {

  public static SmsSendVo toSmsSendVo(Sms sms) {
    SmsSendVo vo = new SmsSendVo();
    vo.setId(sms.getId());
    vo.setPhone(maskPhone(sms.getPhone()));
    vo.setContent(sms.getContent());
    vo.setTemplateId(sms.getTemplateId());
    vo.setStatus(sms.getStatus());
    vo.setSentTime(sms.getSendTime());
    vo.setMessageId(sms.getMessageId());
    return vo;
  }

  public static @NotNull SmsSendBatchVo toSmsSendBatchVo(List<Sms> smsList) {
    SmsSendBatchVo vo = new SmsSendBatchVo();
    vo.setTotalCount(smsList.size());
    vo.setSuccessCount((int) smsList.stream()
        .filter(s -> s.getStatus() != null &&
            (s.getStatus() == SmsStatus.SENT ||
                s.getStatus() == SmsStatus.DELIVERED))
        .count());
    vo.setFailedCount(vo.getTotalCount() - vo.getSuccessCount());
    vo.setResults(smsList.stream().map(sms -> {
      SmsSendBatchVo.SmsSendResultVo result = new SmsSendBatchVo.SmsSendResultVo();
      result.setPhone(maskPhone(sms.getPhone()));
      result.setStatus(sms.getStatus());
      result.setMessageId(sms.getMessageId());
      return result;
    }).collect(Collectors.toList()));
    return vo;
  }

  public static SmsTestVo toSmsTestVo(Sms sms) {
    SmsTestVo vo = new SmsTestVo();
    vo.setPhone(maskPhone(sms.getPhone()));
    vo.setStatus(sms.getStatus());
    vo.setMessageId(sms.getMessageId());
    vo.setSentTime(sms.getSendTime());
    return vo;
  }

  public static SmsRecordVo toRecordVo(Sms sms) {
    SmsRecordVo vo = new SmsRecordVo();
    vo.setId(sms.getId());
    vo.setPhone(maskPhone(sms.getPhone()));
    vo.setContent(maskDigits(sms.getContent()));
    vo.setTemplateId(sms.getTemplateId());
    //vo.setTemplateName(templateName);
    vo.setStatus(sms.getStatus());
    vo.setSentTime(sms.getSendTime());
    vo.setDeliveredTime(sms.getDeliverTime());
    vo.setProvider(sms.getProvider());

    // 设置审计字段
    vo.setTenantId(sms.getTenantId());
    vo.setCreatedBy(sms.getCreatedBy());
    vo.setCreatedDate(sms.getCreatedDate());
    vo.setModifiedBy(sms.getModifiedBy());
    vo.setModifiedDate(sms.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<Sms> getSpecification(SmsRecordFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "sendTime")
        .orderByFields("id", "sendTime")
        .matchSearchFields("phone", "content")
        .build();
    return new GenericSpecification<>(filters);
  }

}
