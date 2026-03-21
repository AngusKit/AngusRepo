package cloud.xcan.angus.core.gm.application.query.sms.impl;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.sms.SmsProviderQuery;
import cloud.xcan.angus.core.gm.application.query.sms.SmsTemplateQuery;
import cloud.xcan.angus.core.gm.domain.sms.SmsProvider;
import cloud.xcan.angus.core.gm.domain.sms.SmsRepo;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplate;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplateRepo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class SmsTemplateQueryImpl implements SmsTemplateQuery {

  @Resource
  private SmsTemplateRepo smsTemplateRepo;

  @Resource
  private SmsRepo smsRepo;

  @Resource
  private SmsProviderQuery smsProviderQuery;

  @Override
  public SmsTemplate findAndCheck(Long id) {
    return new BizTemplate<SmsTemplate>() {
      @Override
      protected SmsTemplate process() {
        return smsTemplateRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("短信模板「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<SmsTemplate> findTemplates(GenericSpecification<SmsTemplate> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<SmsTemplate>>() {
      @Override
      protected Page<SmsTemplate> process() {
        Page<SmsTemplate> page = smsTemplateRepo.findAll(spec, pageable);

        // 批量设置关联数据
        if (!page.isEmpty()) {
          assembleTemplateInfos(page.getContent());
        }

        return page;
      }
    }.execute();
  }

  @Override
  public SmsTemplate findAndCheck(String code) {
    return new BizTemplate<SmsTemplate>() {
      @Override
      protected SmsTemplate process() {
        // 查询所有匹配的模板
        List<SmsTemplate> templates = smsTemplateRepo.findAllByCode(code);
        if (templates.isEmpty()) {
          throw ResourceNotFound.of("短信模板「{0}」不存在", new Object[]{code});
        }

        // 获取默认服务商
        Optional<SmsProvider> defaultProviderOpt;
        try {
          defaultProviderOpt = Optional.of(smsProviderQuery.findDefaultProvider());
        } catch (Exception e) {
          defaultProviderOpt = Optional.empty();
        }

        // 优先返回启用且属于默认服务商的模板
        if (defaultProviderOpt.isPresent()) {
          SmsProvider defaultProvider = defaultProviderOpt.get();
          String defaultProviderName = defaultProvider.getName();

          // 检查默认服务商是否启用
          if (EnabledStatus.ENABLED.equals(defaultProvider.getStatus())) {
            Optional<SmsTemplate> defaultProviderTemplate = templates.stream()
                .filter(t -> defaultProviderName.equals(t.getProvider())
                    && EnabledStatus.ENABLED.equals(t.getStatus()))
                .findFirst();

            if (defaultProviderTemplate.isPresent()) {
              return defaultProviderTemplate.get();
            }
          }
        }

        // 如果找不到默认服务商的模板，返回第一个启用的模板
        Optional<SmsTemplate> enabledTemplate = templates.stream()
            .filter(t -> EnabledStatus.ENABLED.equals(t.getStatus()))
            .findFirst();

        if (enabledTemplate.isPresent()) {
          return enabledTemplate.get();
        }

        // 如果都没有启用，返回第一个模板（向后兼容）
        return templates.get(0);
      }
    }.execute();
  }

  @Override
  public SmsTemplate findAndCheck(String code, Language language) {
    return new BizTemplate<SmsTemplate>() {
      @Override
      protected SmsTemplate process() {
        // 查询所有匹配的模板
        List<SmsTemplate> templates = smsTemplateRepo.findAllByCodeAndLanguage(code, language);
        if (templates.isEmpty()) {
          throw ResourceNotFound.of("短信模板「{0}」在语言「{1}」下不存在",
              new Object[]{code, language});
        }

        // 获取默认服务商
        Optional<SmsProvider> defaultProviderOpt;
        try {
          defaultProviderOpt = Optional.of(smsProviderQuery.findDefaultProvider());
        } catch (Exception e) {
          defaultProviderOpt = Optional.empty();
        }

        // 优先返回启用且属于默认服务商的模板
        if (defaultProviderOpt.isPresent()) {
          SmsProvider defaultProvider = defaultProviderOpt.get();
          String defaultProviderName = defaultProvider.getName();

          // 检查默认服务商是否启用
          if (EnabledStatus.ENABLED.equals(defaultProvider.getStatus())) {
            Optional<SmsTemplate> defaultProviderTemplate = templates.stream()
                .filter(t -> defaultProviderName.equals(t.getProvider())
                    && EnabledStatus.ENABLED.equals(t.getStatus()))
                .findFirst();

            if (defaultProviderTemplate.isPresent()) {
              return defaultProviderTemplate.get();
            }
          }
        }

        // 如果找不到默认服务商的模板，返回第一个启用的模板
        Optional<SmsTemplate> enabledTemplate = templates.stream()
            .filter(t -> EnabledStatus.ENABLED.equals(t.getStatus()))
            .findFirst();

        if (enabledTemplate.isPresent()) {
          return enabledTemplate.get();
        }

        // 如果都没有启用，返回第一个模板（向后兼容）
        return templates.get(0);
      }
    }.execute();
  }

  @Override
  public SmsTemplate findAndCheck(String provider, String code, Language language) {
    String finalProvider = provider != null ? provider : "";
    return smsTemplateRepo.findByProviderAndCodeAndLanguage(finalProvider, code, language)
        .orElseThrow(() -> ResourceNotFound.of("短信模板在通道「{0}」、编码「{1}」、语言「{2}」下不存在",
            new Object[]{finalProvider, code, language}));
  }

  @Override
  public void assembleTemplateInfos(List<SmsTemplate> templates) {
    if (templates == null || templates.isEmpty()) {
      return;
    }

    // 批量查询使用次数
    Set<Long> templateIds = templates.stream().map(SmsTemplate::getId).collect(Collectors.toSet());
    Map<Long, Long> usageCountMap = templateIds.stream()
        .collect(Collectors.toMap(id -> id, smsRepo::countByTemplateId));

    // 设置使用次数
    for (SmsTemplate template : templates) {
      template.setUsageCount(usageCountMap.getOrDefault(template.getId(), 0L));
    }
  }
}
