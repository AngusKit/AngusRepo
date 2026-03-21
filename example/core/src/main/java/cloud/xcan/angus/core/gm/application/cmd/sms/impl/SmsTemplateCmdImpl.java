package cloud.xcan.angus.core.gm.application.cmd.sms.impl;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsTemplateCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsTemplateQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplate;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplateRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SmsTemplateCmdImpl extends CommCmd<SmsTemplate, Long> implements SmsTemplateCmd {

  @Resource
  private SmsTemplateRepo smsTemplateRepo;

  @Resource
  private SmsTemplateQuery smsTemplateQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SmsTemplate createTemplate(SmsTemplate template) {
    return new BizTemplate<SmsTemplate>(false) {
      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        String provider = template.getProvider() != null ? template.getProvider() : "";
        if (smsTemplateRepo.existsByProviderAndCodeAndLanguage(provider, template.getCode(),
            template.getLanguage())) {
          throw ResourceExisted.of("短信模板在通道「{0}」、编码「{1}」、语言「{2}」下已存在",
              new Object[]{provider, template.getCode(), template.getLanguage()});
        }
      }

      @Override
      protected SmsTemplate process() {
        insert(template);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.CONFIG,
            template.getId(),
            template.getName(),
            OperationMessage.SMS_TEMPLATE_CREATE_DETAILS,
            new Object[]{template.getName()}
        );

        return template;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SmsTemplate updateTemplate(SmsTemplate template) {
    return new BizTemplate<SmsTemplate>(false) {
      SmsTemplate existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        existing = smsTemplateQuery.findAndCheck(template.getId());

        // 如果通道、编码或语言被更新，检查是否重复
        String existingProvider = existing.getProvider() != null ? existing.getProvider() : "";
        String newProvider = template.getProvider() != null ? template.getProvider() : "";
        boolean providerChanged = !existingProvider.equals(newProvider);
        boolean codeChanged =
            template.getCode() != null && !existing.getCode().equals(template.getCode());
        boolean languageChanged = template.getLanguage() != null && !existing.getLanguage()
            .equals(template.getLanguage());

        if ((providerChanged || codeChanged || languageChanged)) {
          String finalProvider =
              template.getProvider() != null ? template.getProvider() : existingProvider;
          String finalCode = template.getCode() != null ? template.getCode() : existing.getCode();
          Language finalLanguage =
              template.getLanguage() != null ? template.getLanguage() : existing.getLanguage();

          if (smsTemplateRepo.existsByProviderAndCodeAndLanguageAndIdNot(finalProvider, finalCode,
              finalLanguage,
              template.getId())) {
            throw ResourceExisted.of("短信模板在通道「{0}」、编码「{1}」、语言「{2}」下已存在",
                new Object[]{finalProvider, finalCode,
                    finalLanguage != null ? finalLanguage.getValue() : ""});
          }
        }
      }

      @Override
      protected SmsTemplate process() {
        // 更新字段
        CoreUtils.copyPropertiesIgnoreNull(template, existing);
        SmsTemplate saved = smsTemplateRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.SMS_TEMPLATE_UPDATE_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SmsTemplate updateTemplateStatus(Long id, EnabledStatus status) {
    return new BizTemplate<SmsTemplate>(false) {
      SmsTemplate existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        existing = smsTemplateQuery.findAndCheck(id);
      }

      @Override
      protected SmsTemplate process() {
        existing.setStatus(status);
        SmsTemplate saved = smsTemplateRepo.save(existing);

        // 记录操作日志
        String statusText = status.isEnabled() ? "YES" : "NO";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.SMS_TEMPLATE_UPDATE_STATUS_DETAILS,
            new Object[]{saved.getName(), statusText}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteTemplate(Long id) {
    new BizTemplate<Void>(false) {
      SmsTemplate existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        existing = smsTemplateQuery.findAndCheck(id);
      }

      @Override
      protected Void process() {
        String templateName = existing.getName();

        smsTemplateRepo.deleteById(id);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.CONFIG,
            id,
            templateName,
            OperationMessage.SMS_TEMPLATE_DELETE_DETAILS,
            new Object[]{templateName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<SmsTemplate, Long> getRepository() {
    return smsTemplateRepo;
  }
}
