package cloud.xcan.angus.core.gm.application.cmd.email.impl;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailTemplateCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplate;
import cloud.xcan.angus.core.gm.domain.email.EmailTemplateRepo;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailTemplateCmdImpl extends CommCmd<EmailTemplate, Long> implements EmailTemplateCmd {

  @Resource
  private EmailTemplateRepo emailTemplateRepo;

  @Resource
  private EmailTemplateQuery emailTemplateQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public EmailTemplate create(EmailTemplate template) {
    return new BizTemplate<EmailTemplate>(false) {
      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();

        if (emailTemplateQuery.existsByCodeAndLanguage(template.getCode(),
            template.getLanguage())) {
          throw ResourceExisted.of("邮件模板编码「{0}」和语言「{1}」的组合已存在",
              new Object[]{template.getCode(), template.getLanguage()});
        }
      }

      @Override
      protected EmailTemplate process() {
        template.setIsSystem(false);
        insert(template);

        // 记录操作日志
        String templateName = template.getName();
        String templateCode = template.getCode() != null ? template.getCode() : "";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.CONFIG,
            template.getId(),
            templateName,
            OperationMessage.EMAIL_TEMPLATE_CREATE_DETAILS,
            new Object[]{templateName, templateCode}
        );

        return template;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public EmailTemplate update(EmailTemplate template) {
    return new BizTemplate<EmailTemplate>(false) {
      EmailTemplate templateDb;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();

        templateDb = emailTemplateQuery.findAndCheck(template.getId());

        if ((template.getCode() != null && !template.getCode().equals(templateDb.getCode()))
            || (template.getLanguage() != null && !template.getLanguage()
            .equals(templateDb.getLanguage()))) {
          String code = template.getCode() != null ? template.getCode() : templateDb.getCode();
          Language lang = template.getLanguage() != null
              ? template.getLanguage() : templateDb.getLanguage();
          if (emailTemplateQuery.existsByCodeAndLanguageAndIdNot(code, lang,
              template.getId())) {
            throw ResourceExisted.of("邮件模板编码「{0}」和语言「{1}」的组合已存在",
                new Object[]{code, lang != null ? lang.getValue() : ""});
          }
        }
      }

      @Override
      protected EmailTemplate process() {
        // 系统模版状态不允许被修改
        template.setIsSystem(templateDb.getIsSystem());
        update(template, templateDb);

        // 记录操作日志
        String templateName = templateDb.getName();
        String templateCode = templateDb.getCode() != null ? templateDb.getCode() : "";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            template.getId(),
            templateName,
            OperationMessage.EMAIL_TEMPLATE_UPDATE_DETAILS,
            new Object[]{templateName, templateCode}
        );

        return templateDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public EmailTemplate updateStatus(Long id, EnabledStatus status) {
    return new BizTemplate<EmailTemplate>(false) {
      EmailTemplate templateDb;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        templateDb = emailTemplateQuery.findAndCheck(id);
      }

      @Override
      protected EmailTemplate process() {
        templateDb.setStatus(status);
        emailTemplateRepo.save(templateDb);

        // 记录操作日志
        String templateName = templateDb.getName();
        if (EnabledStatus.ENABLED.equals(status)) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.CONFIG,
              id,
              templateName,
              OperationMessage.EMAIL_TEMPLATE_ENABLE_DETAILS,
              new Object[]{templateName}
          );
        } else if (EnabledStatus.DISABLED.equals(status)) {
          userOperationLogCmd.logSuccessByMessageKey(
              OperationAction.UPDATE,
              ResourceType.CONFIG,
              id,
              templateName,
              OperationMessage.EMAIL_TEMPLATE_DISABLE_DETAILS,
              new Object[]{templateName}
          );
        }

        return templateDb;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>(false) {
      EmailTemplate templateDb;

      @Override
      protected void checkParams() {
        templateDb = emailTemplateQuery.findAndCheck(id);
        if (Boolean.TRUE.equals(templateDb.getIsSystem())) {
          throw ProtocolException.of("系统模板「{0}」不允许删除", new Object[]{templateDb.getName()});
        }
      }

      @Override
      protected Void process() {
        // 保存模板名称用于操作日志（删除前获取）
        String templateName = templateDb.getName();

        emailTemplateRepo.deleteById(id);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.CONFIG,
            id,
            templateName,
            OperationMessage.EMAIL_TEMPLATE_DELETE_DETAILS,
            new Object[]{templateName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<EmailTemplate, Long> getRepository() {
    return emailTemplateRepo;
  }
}

