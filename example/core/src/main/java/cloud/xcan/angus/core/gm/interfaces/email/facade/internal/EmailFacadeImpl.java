package cloud.xcan.angus.core.gm.interfaces.email.facade.internal;

import static cloud.xcan.angus.api.commonlink.GMConstant.DEFAULT_EMAIL_LANGUAGE;
import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler.toEmailSendDto;
import static cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler.toEmailSendResultVo;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;
import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.gm.email.dto.EmailSendBatchDto;
import cloud.xcan.angus.api.gm.email.dto.EmailSendDto;
import cloud.xcan.angus.api.gm.email.vo.EmailSendBatchVo;
import cloud.xcan.angus.api.gm.email.vo.EmailSendBatchVo.EmailSendResultVo;
import cloud.xcan.angus.api.gm.email.vo.EmailSendVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailQuery;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.application.query.email.EmailTrackingQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.interfaces.email.facade.EmailFacade;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.dto.EmailSendCustomDto;
import cloud.xcan.angus.core.gm.interfaces.email.facade.internal.assembler.EmailAssembler;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailRecordVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailStatsVo;
import cloud.xcan.angus.core.gm.interfaces.email.facade.vo.EmailTrackingVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailFacadeImpl implements EmailFacade {

  @Resource
  private EmailCmd emailCmd;

  @Resource
  private EmailQuery emailQuery;

  @Resource
  private EmailTemplateQuery emailTemplateQuery;

  @Resource
  private EmailTrackingQuery emailTrackingQuery;

  @Override
  public EmailSendVo send(EmailSendDto dto) {
    EmailTemplate template = emailTemplateQuery.findAndCheckValid(
        dto.getTemplateCode(),
        Language.fromValue(nullSafe(dto.getLanguage(), DEFAULT_EMAIL_LANGUAGE)));
    Email email = EmailAssembler.toSendEmailDomain(dto, template);
    Email created = emailCmd.create(email);
    emailCmd.send(created.identity(), false);
    return EmailAssembler.toSendVo(created);
  }

  @Override
  public EmailSendBatchVo sendBatch(EmailSendBatchDto dto) {
    EmailTemplate template = emailTemplateQuery.findAndCheckValid(
        dto.getTemplateCode(),
        Language.fromValue(nullSafe(dto.getLanguage(), DEFAULT_EMAIL_LANGUAGE)));
    EmailSendBatchVo vo = new EmailSendBatchVo();
    vo.setTotalCount(dto.getTo().size());
    vo.setSuccessCount(0);
    vo.setFailedCount(0);
    List<EmailSendBatchVo.EmailSendResultVo> results = new ArrayList<>();

    // 批量创建邮件记录（状态为PENDING），由Job异步发送
    for (String to : dto.getTo()) {
      try {
        EmailSendDto singleDto = toEmailSendDto(dto, to);
        Email email = EmailAssembler.toSendEmailDomain(singleDto, template);
        Email created = emailCmd.create(email);
        // 邮件创建成功，状态为PENDING，等待Job发送
        EmailSendResultVo result = toEmailSendResultVo(to, created);
        results.add(result);
        vo.setSuccessCount(vo.getSuccessCount() + 1);
      } catch (Exception e) {
        log.error("创建邮件记录失败: to={}", to, e);
        EmailSendResultVo result = toEmailSendResultVo(to, e);
        results.add(result);
        vo.setFailedCount(vo.getFailedCount() + 1);
      }
    }
    vo.setResults(results);
    return vo;
  }

  @Override
  public EmailSendVo retry(Long id) {
    Email saved = emailCmd.retry(id);
    return EmailAssembler.toSendVo(saved);
  }

  @Override
  public EmailSendVo cancel(Long id) {
    Email saved = emailCmd.cancel(id);
    return EmailAssembler.toSendVo(saved);
  }

  @Override
  public EmailSendVo sendCustom(EmailSendCustomDto dto) {
    Email email = EmailAssembler.toCustomEmailDomain(dto);
    Email created = emailCmd.create(email);
    emailCmd.send(created.identity(), true);
    return EmailAssembler.toSendVo(created);
  }

  @Override
  public EmailStatsVo getStats() {
    return emailQuery.getStatistics();
  }

  @NameJoin
  @Override
  public PageResult<EmailRecordVo> listRecords(EmailRecordFindDto dto) {
    var spec = EmailAssembler.getRecordSpecification(dto);
    Page<Email> page = emailQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, EmailAssembler::toRecordVo);
  }

  @Override
  public EmailTrackingVo getEmailTracking(Long id) {
    Email email = emailQuery.findAndCheck(id);
    var tracking = emailTrackingQuery.findByEmailId(id);
    return EmailAssembler.toTrackingVo(email, tracking.orElse(null));
  }

}
