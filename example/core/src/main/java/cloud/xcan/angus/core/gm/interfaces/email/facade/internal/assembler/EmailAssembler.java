package cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.maskDigits;

import cloud.xcan.angus.api.commonlink.email.EmailStatus;
import cloud.xcan.angus.api.gm.email.dto.EmailSendBatchDto;
import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.vo.EmailSendBatchVo.EmailSendResultVo;
import cloud.xcan.angus.api.gm.email.vo.EmailSendVo;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.domain.email.EmailTracking;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendCustomDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailRecordVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTrackingVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 邮件数据转换器
 */
public class EmailAssembler {

  public static EmailSendVo toSendVo(Email email) {
    EmailSendVo vo = new EmailSendVo();
    vo.setId(email.getId());
    if (email.getToRecipients() != null && !email.getToRecipients().isEmpty()) {
      vo.setTo(email.getToRecipients().get(0));
    }
    vo.setSubject(email.getSubject());
    vo.setTemplateId(email.getTemplateId());
    vo.setStatus(email.getStatus());
    vo.setSentTime(email.getSendTime());
    vo.setMessageId(email.getExternalId());

    // 设置审计字段
    vo.setTenantId(email.getTenantId());
    vo.setCreatedBy(email.getCreatedBy());
    vo.setCreatedDate(email.getCreatedDate());
    vo.setModifiedBy(email.getModifiedBy());
    vo.setModifiedDate(email.getModifiedDate());
    return vo;
  }

  public static EmailRecordVo toRecordVo(Email email) {
    EmailRecordVo vo = new EmailRecordVo();
    vo.setId(email.getId());
    if (email.getToRecipients() != null && !email.getToRecipients().isEmpty()) {
      vo.setTo(email.getToRecipients().get(0));
    }
    if (email.getCcRecipients() != null && !email.getCcRecipients().isEmpty()) {
      vo.setCc(email.getCcRecipients().get(0));
    }
    if (email.getBccRecipients() != null && !email.getBccRecipients().isEmpty()) {
      vo.setBcc(email.getBccRecipients().get(0));
    }
    vo.setSubject(email.getSubject());
    vo.setContent(maskDigits(email.getHtmlContent()));
    vo.setTemplateId(email.getTemplateId());
    vo.setStatus(email.getStatus());
    vo.setSentTime(email.getSendTime());
    vo.setDeliveredTime(email.getDeliverTime());

    // Convert attachments
    if (email.getAttachments() != null) {
      List<EmailRecordVo.EmailAttachmentVo> attachmentVos = email.getAttachments().stream()
          .map(att -> {
            EmailRecordVo.EmailAttachmentVo attachmentVo = new EmailRecordVo.EmailAttachmentVo();
            attachmentVo.setFileName((String) att.get("fileName"));
            attachmentVo.setFileUrl((String) att.get("fileUrl"));
            return attachmentVo;
          })
          .collect(Collectors.toList());
      vo.setAttachments(attachmentVos);
    }

    // 设置审计字段
    vo.setTenantId(email.getTenantId());
    vo.setCreatedBy(email.getCreatedBy());
    vo.setCreatedDate(email.getCreatedDate());
    vo.setModifiedBy(email.getModifiedBy());
    vo.setModifiedDate(email.getModifiedDate());
    return vo;
  }

  public static EmailTrackingVo toTrackingVo(Email email, EmailTracking tracking) {
    EmailTrackingVo vo = new EmailTrackingVo();
    vo.setEmailId(email.getId());
    vo.setSubject(email.getSubject());
    vo.setSentTime(email.getSendTime());
    vo.setDeliveredTime(email.getDeliverTime());

    if (tracking != null) {
      vo.setOpened(Boolean.TRUE.equals(tracking.getOpened()));
      vo.setOpenedTime(tracking.getOpenedTime());
      vo.setOpenCount(tracking.getOpenCount() != null ? tracking.getOpenCount() : 0);
      vo.setClicked(Boolean.TRUE.equals(tracking.getClicked()));
      vo.setClickCount(tracking.getClickCount() != null ? tracking.getClickCount() : 0);
      vo.setBounced(Boolean.TRUE.equals(tracking.getBounced()));
      vo.setComplained(Boolean.TRUE.equals(tracking.getComplained()));
    } else {
      vo.setOpened(false);
      vo.setOpenedTime(null);
      vo.setOpenCount(0);
      vo.setClicked(false);
      vo.setClickCount(0);
      vo.setBounced(false);
      vo.setComplained(false);
    }
    return vo;
  }

  public static Email toSendEmailDomain(EmailSendDto dto, EmailTemplate template) {
    Email email = new Email();
    email.setToRecipients(List.of(dto.getTo()));
    if (dto.getCc() != null) {
      email.setCcRecipients(List.of(dto.getCc()));
    }
    if (dto.getBcc() != null) {
      email.setBccRecipients(List.of(dto.getBcc()));
    }
    email.setSubject(template.getSubject());
    email.setTemplateId(template.getId());
    if (dto.getParams() != null) {
      Map<String, Object> templateParams = new HashMap<>(dto.getParams());
      email.setTemplateParams(templateParams);
    }
    if (dto.getAttachments() != null) {
      List<Map<String, Object>> attachments = dto.getAttachments().stream()
          .map(att -> {
            Map<String, Object> attMap = new HashMap<>();
            attMap.put("fileName", att.getFileName());
            attMap.put("fileUrl", att.getFileUrl());
            return attMap;
          })
          .collect(Collectors.toList());
      email.setAttachments(attachments);
    }
    return email;
  }

  public static Email toCustomEmailDomain(EmailSendCustomDto dto) {
    Email email = new Email();
    email.setToRecipients(List.of(dto.getTo()));
    if (dto.getCc() != null) {
      email.setCcRecipients(List.of(dto.getCc()));
    }
    if (dto.getBcc() != null) {
      email.setBccRecipients(List.of(dto.getBcc()));
    }
    email.setSubject(dto.getSubject());
    if ("html".equals(dto.getContentType())) {
      email.setHtmlContent(dto.getContent());
    } else {
      email.setTextContent(dto.getContent());
    }
    if (dto.getAttachments() != null) {
      List<Map<String, Object>> attachments = dto.getAttachments().stream()
          .map(att -> {
            Map<String, Object> attMap = new HashMap<>();
            attMap.put("fileName", att.getFileName());
            attMap.put("fileUrl", att.getFileUrl());
            return attMap;
          })
          .collect(Collectors.toList());
      email.setAttachments(attachments);
    }
    return email;
  }

  public static EmailSendResultVo toEmailSendResultVo(String to, Exception e) {
    EmailSendResultVo result = new EmailSendResultVo();
    result.setTo(to);
    result.setStatus(EmailStatus.FAILED);
    result.setMessageId(e.getMessage());
    return result;
  }

  public static EmailSendResultVo toEmailSendResultVo(String to, Email created) {
    EmailSendResultVo result = new EmailSendResultVo();
    result.setTo(to);
    // 返回邮件的实际状态（创建时为PENDING，由Job异步发送）
    result.setStatus(created.getStatus());
    result.setMessageId(created.getExternalId());
    return result;
  }

  public static EmailSendDto toEmailSendDto(EmailSendBatchDto dto, String to) {
    EmailSendDto singleDto = new EmailSendDto();
    singleDto.setTo(to);
    singleDto.setTemplateCode(dto.getTemplateCode());
    singleDto.setParams(dto.getParams());
    return singleDto;
  }

  public static GenericSpecification<Email> getRecordSpecification(EmailRecordFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "sendTime")
        .orderByFields("id", "createdDate", "modifiedDate", "sendTime", "subject")
        .matchSearchFields("subject", "htmlContent", "textContent", "toRecipients")
        .build();
    return new GenericSpecification<>(filters);
  }
}
