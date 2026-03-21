package cloud.xcan.angus.core.gm.application.query.email.impl;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.email.EmailStatus;
import cloud.xcan.angus.api.manager.TenantManager;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.email.EmailRepo;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplateRepo;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplateSearchRepo;
import cloud.xcan.angus.core.gm.domain.email.EmailTracking;
import cloud.xcan.angus.core.gm.domain.email.EmailTrackingRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateQueryImpl implements EmailTemplateQuery {

  @Resource
  private EmailTemplateRepo emailTemplateRepo;

  @Resource
  private EmailTemplateSearchRepo emailTemplateSearchRepo;

  @Resource
  private EmailRepo emailRepo;

  @Resource
  private EmailTrackingRepo emailTrackingRepo;

  @Resource
  private TenantManager tenantManager;

  @Override
  public EmailTemplate findAndCheck(Long id) {
    return new BizTemplate<EmailTemplate>(false) {
      @Override
      protected EmailTemplate process() {
        return emailTemplateRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("邮件模板「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public EmailTemplate findAndCheckValid(String code, @Nullable Language language) {
    return new BizTemplate<EmailTemplate>(false) {
      @Override
      protected EmailTemplate process() {
        Language safeLanguage = language != null ? language : tenantManager.resolveLanguage();
        EmailTemplate template = emailTemplateRepo.findByCodeAndLanguage(code, safeLanguage);
        if (template == null) {
          throw ResourceNotFound.of("邮件模板「{0}」不存在", new Object[]{code});
        }
        if (!template.getStatus().isEnabled()) {
          throw ResourceNotFound.of("邮件模板「{0}」未启用", new Object[]{code});
        }
        return template;
      }
    }.execute();
  }

  @Override
  public Page<EmailTemplate> find(GenericSpecification<EmailTemplate> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<EmailTemplate>>(false) {
      @Override
      protected Page<EmailTemplate> process() {
        return fullTextSearch
            ? emailTemplateSearchRepo.find(spec.getCriteria(), pageable, EmailTemplate.class, match)
            : emailTemplateRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public boolean existsByCodeAndLanguage(String code, Language language) {
    return emailTemplateRepo.existsByCodeAndLanguage(code, language);
  }

  @Override
  public boolean existsByCodeAndLanguageAndIdNot(String code, Language language, Long id) {
    return emailTemplateRepo.existsByCodeAndLanguageAndIdNot(code, language, id);
  }

  @Override
  public void assembleTemplateInfos(List<EmailTemplate> templates) {
    if (templates == null || templates.isEmpty()) {
      return;
    }

    Set<Long> templateIds = templates.stream()
        .map(EmailTemplate::getId)
        .collect(Collectors.toSet());

    // 1. 批量查询使用次数（统计使用该模板发送的邮件数量）
    Map<Long, Long> usageCountMap = new HashMap<>();
    for (Long templateId : templateIds) {
      long count = emailRepo.countByTemplateId(templateId);
      usageCountMap.put(templateId, count);
    }

    // 2. 批量查询邮件记录（用于计算打开率和点击率）
    List<Long> templateIdList = new ArrayList<>(templateIds);

    // 批量查询使用这些模板且状态为已发送的邮件
    List<Email> emails = emailRepo.findByTemplateIdInAndStatus(templateIdList,
        EmailStatus.SENT);

    // 3. 批量查询邮件追踪记录（一个邮件对应一个追踪记录）
    Map<Long, EmailTracking> emailTrackingMap = new HashMap<>();
    if (!emails.isEmpty()) {
      List<Long> emailIds = emails.stream().map(Email::getId).collect(Collectors.toList());
      List<EmailTracking> trackings = emailTrackingRepo.findByEmailIdIn(emailIds);
      emailTrackingMap = trackings.stream()
          .collect(Collectors.toMap(EmailTracking::getEmailId, tracking -> tracking));
    }

    // 4. 按模板ID分组统计打开率和点击率
    Map<Long, List<Email>> emailsByTemplate = emails.stream()
        .filter(email -> email.getTemplateId() != null)
        .collect(Collectors.groupingBy(Email::getTemplateId));

    Map<Long, Double> openRateMap = new HashMap<>();
    Map<Long, Double> clickRateMap = new HashMap<>();

    for (Map.Entry<Long, List<Email>> entry : emailsByTemplate.entrySet()) {
      Long templateId = entry.getKey();
      if (templateId == null) {
        continue;
      }

      List<Email> templateEmails = entry.getValue();
      long totalSent = templateEmails.size();
      if (totalSent == 0) {
        openRateMap.put(templateId, 0.0);
        clickRateMap.put(templateId, 0.0);
        continue;
      }

      // 统计打开数量和点击数量
      long openedCount = 0;
      long clickedCount = 0;
      for (Email email : templateEmails) {
        EmailTracking tracking = emailTrackingMap.get(email.getId());
        if (tracking != null) {
          if (Boolean.TRUE.equals(tracking.getOpened())) {
            openedCount++;
          }
          if (Boolean.TRUE.equals(tracking.getClicked())) {
            clickedCount++;
          }
        }
      }

      // 计算打开率和点击率（百分比，保留2位小数）
      double openRate = openedCount * 100.0 / totalSent;
      double clickRate = clickedCount * 100.0 / totalSent;
      openRateMap.put(templateId, Math.round(openRate * 100.0) / 100.0);
      clickRateMap.put(templateId, Math.round(clickRate * 100.0) / 100.0);
    }

    // 5. 设置统计数据到模板对象
    for (EmailTemplate template : templates) {
      Long templateId = template.getId();
      template.setUsageCount(usageCountMap.getOrDefault(templateId, 0L));
      template.setOpenRate(openRateMap.getOrDefault(templateId, 0.0));
      template.setClickRate(clickRateMap.getOrDefault(templateId, 0.0));
    }
  }
}

