package cloud.xcan.angus.core.gm.application.cmd.sms.impl;

import static cloud.xcan.angus.api.commonlink.GMConstant.TEMPLATE_CODE_CHANNEL_TEST;
import static cloud.xcan.angus.core.gm.application.converter.SmsConverter.replaceTemplateParams;
import static cloud.xcan.angus.core.gm.application.converter.SmsConverter.toBatchSendSms;
import static cloud.xcan.angus.core.gm.application.converter.SmsConverter.toSendSms;
import static cloud.xcan.angus.core.gm.application.converter.SmsConverter.toTestSms;
import static cloud.xcan.angus.core.gm.infra.plugin.SmsPluginStateListener.getPluginProvider;
import static cloud.xcan.angus.core.gm.infra.plugin.SmsPluginStateListener.toMessageProvider;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getOptTenantId;
import static cloud.xcan.angus.spec.experimental.SimpleResult.SUCCESS_CODE;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.sms.SmsStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsProviderQuery;
import cloud.xcan.angus.core.gm.application.query.sms.SmsTemplateQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.sms.SmsProvider;
import cloud.xcan.angus.core.gm.domain.sms.SmsRepo;
import cloud.xcan.angus.core.gm.domain.sms.SmsTemplate;
import cloud.xcan.angus.extension.sms.api.MessageProvider;
import cloud.xcan.angus.extension.sms.api.SmsProviderExtension;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.spec.experimental.SimpleResult;
import cloud.xcan.angus.spec.locale.SdfLocaleHolder;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SmsCmdImpl implements SmsCmd {

  @Resource
  private SmsRepo smsRepo;

  @Resource
  private SmsTemplateQuery smsTemplateQuery;

  @Resource
  private SmsProviderQuery smsProviderQuery;

  @Resource
  private UserQuery userQuery;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Sms send(String templateCode, Language language, String phone,
      Map<String, String> params) {
    return new BizTemplate<Sms>() {
      SmsTemplate templateDb;
      SmsProvider providerDb;
      SmsProviderExtension pluginProvider;
      MessageProvider messageProvider;

      @Override
      protected void checkParams() {
        // 获取默认服务商
        providerDb = smsProviderQuery.findDefaultProvider();
        String defaultProviderName = providerDb != null ? providerDb.getName() : "";

        // 查找模板：先尝试默认服务商，如果找不到则尝试空字符串（向后兼容）
        try {
          templateDb = smsTemplateQuery.findAndCheck(defaultProviderName, templateCode,
              language);
        } catch (Exception e) {
          // 如果默认服务商找不到模板，尝试使用空字符串（向后兼容旧数据）
          try {
            templateDb = smsTemplateQuery.findAndCheck("", templateCode, language);
          } catch (Exception e2) {
            // 如果都找不到，抛出原始异常
            throw ProtocolException.of("短信模板「{0}」在语言「{1}」下不存在",
                new Object[]{templateCode, language.getValue()});
          }
        }

        if (!EnabledStatus.ENABLED.equals(templateDb.getStatus())) {
          throw ProtocolException.of("短信模板「{0}」未启用",
              new Object[]{templateDb.getName()});
        }

        // 获取服务商（优先使用模板配置的服务商，否则使用默认服务商）
        String providerName = templateDb.getProvider();
        if (isEmpty(providerName)) {
          // 如果模板的 provider 为空字符串，使用默认服务商
          if (providerDb == null) {
            providerDb = smsProviderQuery.findDefaultProvider();
          }
        } else {
          // 如果模板指定了服务商，使用指定的服务商
          List<SmsProvider> providers = smsProviderQuery.listProviders();
          providerDb = providers.stream()
              .filter(p -> providerName.equals(p.getName())
                  && EnabledStatus.ENABLED.equals(p.getStatus()))
              .findFirst()
              .orElseThrow(() -> ProtocolException.of("短信服务商「{0}」未启用或不存在",
                  new Object[]{providerName}));
        }

        // 获取插件实例
        pluginProvider = getPluginProvider(providerDb.getName());
        if (pluginProvider == null) {
          throw ProtocolException.of("未找到短信服务商「{0}」的插件",
              new Object[]{providerDb.getName()});
        }

        // 转换为 MessageProvider
        messageProvider = toMessageProvider(providerDb);
      }

      @Override
      protected Sms process() {
        // 替换模板参数
        String content = replaceTemplateParams(templateDb.getContent(), params);

        // 创建短信实体
        Sms sms = toSendSms(templateDb, providerDb, phone, content, params);

        // 调用插件发送短信
        cloud.xcan.angus.extension.sms.api.Sms extensionSms
            = new cloud.xcan.angus.extension.sms.api.Sms();
        extensionSms.setPhones(List.of(phone));
        extensionSms.setTemplateCode(templateDb.getTemplateCode());
        extensionSms.setTemplateParams(params);
        extensionSms.setSign(templateDb.getSignature()); // 签名由服务商配置

        SimpleResult result = pluginProvider.sendSms(extensionSms, messageProvider);

        // 更新发送结果
        if (SUCCESS_CODE.equals(result.getCode())) {
          sms.setStatus(SmsStatus.SENT);
          sms.setMessageId(result.getMessage());
        } else {
          sms.setStatus(SmsStatus.FAILED);
          sms.setErrorCode(result.getCode());
          sms.setErrorMessage(result.getMessage());
        }

        if (PrincipalContext.getApiType().isUserTypeApi()) {
          sms.setTenantId(getOptTenantId());
        } else {
          sms.setTenantId(userQuery.findTenantIdByPhone(phone));
        }
        return smsRepo.save(sms);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public List<Sms> sendBatch(String templateCode, Language language, List<String> phones,
      Map<String, String> params) {
    return new BizTemplate<List<Sms>>() {
      SmsTemplate templateDb;
      SmsProvider providerDb;
      SmsProviderExtension pluginProvider;
      MessageProvider messageProvider;

      @Override
      protected void checkParams() {
        if (isEmpty(phones)) {
          throw ProtocolException.of("手机号列表不能为空", new Object[]{});
        }

        // 获取默认服务商
        providerDb = smsProviderQuery.findDefaultProvider();
        String defaultProviderName = providerDb != null ? providerDb.getName() : "";

        // 查找模板：先尝试默认服务商，如果找不到则尝试空字符串（向后兼容）
        try {
          templateDb = smsTemplateQuery.findAndCheck(defaultProviderName, templateCode,
              language);
        } catch (Exception e) {
          // 如果默认服务商找不到模板，尝试使用空字符串（向后兼容旧数据）
          try {
            templateDb = smsTemplateQuery.findAndCheck("", templateCode, language);
          } catch (Exception e2) {
            // 如果都找不到，抛出原始异常
            throw ProtocolException.of("短信模板「{0}」在语言「{1}」下不存在",
                new Object[]{templateCode, language.getValue()});
          }
        }

        if (!EnabledStatus.ENABLED.equals(templateDb.getStatus())) {
          throw ProtocolException.of("短信模板「{0}」未启用",
              new Object[]{templateDb.getName()});
        }

        // 获取服务商（优先使用模板配置的服务商，否则使用默认服务商）
        String providerName = templateDb.getProvider();
        if (isEmpty(providerName)) {
          providerDb = smsProviderQuery.findDefaultProvider();
        } else {
          List<SmsProvider> providers = smsProviderQuery.listProviders();
          providerDb = providers.stream()
              .filter(p -> providerName.equals(p.getName())
                  && EnabledStatus.ENABLED.equals(p.getStatus()))
              .findFirst()
              .orElseThrow(() -> ProtocolException.of("短信服务商「{0}」未启用或不存在",
                  new Object[]{providerName}));
        }

        // 获取插件实例
        pluginProvider = getPluginProvider(providerDb.getName());
        if (pluginProvider == null) {
          throw ProtocolException.of("未找到短信服务商「{0}」的插件",
              new Object[]{providerDb.getName()});
        }

        // 转换为 MessageProvider
        messageProvider = toMessageProvider(providerDb);
      }

      @Override
      protected List<Sms> process() {
        List<Sms> smsList = new ArrayList<>();

        // 替换模板参数
        String content = replaceTemplateParams(templateDb.getContent(), params);

        // 如果有签名，追加到内容后面
        if (templateDb.getSignature() != null && !templateDb.getSignature().isEmpty()) {
          content = content + templateDb.getSignature();
        }

        // 批量发送（最多500条，超过则分批）
        int batchSize = 500;
        for (int i = 0; i < phones.size(); i += batchSize) {
          int end = Math.min(i + batchSize, phones.size());
          List<String> batchPhones = phones.subList(i, end);

          // 调用插件发送短信
          cloud.xcan.angus.extension.sms.api.Sms extensionSms
              = new cloud.xcan.angus.extension.sms.api.Sms();
          extensionSms.setPhones(batchPhones);
          extensionSms.setTemplateCode(templateDb.getTemplateCode());
          extensionSms.setTemplateParams(params);
          extensionSms.setSign(templateDb.getSignature()); // 签名由服务商配置

          SimpleResult result = pluginProvider.sendSms(extensionSms, messageProvider);

          // 为每个手机号创建短信记录
          for (String phone : batchPhones) {
            Sms sms = toBatchSendSms(templateDb, providerDb, phone, content, params, result);
            smsList.add(smsRepo.save(sms));
          }
        }
        return smsList;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Sms test(String phone, String content) {
    return new BizTemplate<Sms>() {
      SmsProvider providerDb;
      SmsProviderExtension pluginProvider;
      MessageProvider messageProvider;

      @Override
      protected void checkParams() {
        // 获取默认服务商
        providerDb = smsProviderQuery.findDefaultProvider();
        if (!EnabledStatus.ENABLED.equals(providerDb.getStatus())) {
          throw ProtocolException.of("默认短信服务商「{0}」未启用",
              new Object[]{providerDb.getName()});
        }

        // 获取插件实例
        pluginProvider = getPluginProvider(providerDb.getName());
        if (pluginProvider == null) {
          throw ProtocolException.of("未找到短信服务商「{0}」的插件",
              new Object[]{providerDb.getName()});
        }

        // 转换为 MessageProvider
        messageProvider = toMessageProvider(providerDb);
      }

      @Override
      protected Sms process() {
        // 创建短信实体
        Sms sms = toTestSms(providerDb, phone, content);

        // 确定语言，默认为中文
        String language = SdfLocaleHolder.getLocale().getLanguage();
        Language finalLanguage = Language.fromValue(
            (language != null && !language.isEmpty()) ? language : "zh_CN");
        SmsTemplate template = smsTemplateQuery.findAndCheck(TEMPLATE_CODE_CHANNEL_TEST,
            finalLanguage);

        // 调用插件发送短信（测试短信直接发送内容，不使用模板）
        cloud.xcan.angus.extension.sms.api.Sms extensionSms
            = new cloud.xcan.angus.extension.sms.api.Sms();
        extensionSms.setPhones(List.of(phone));
        extensionSms.setTemplateCode(template.getTemplateCode());
        extensionSms.setTemplateParams(Map.of("channelType", providerDb.getName()));
        extensionSms.setSign(template.getSignature()); // 签名由服务商配置

        SimpleResult result = pluginProvider.sendSms(extensionSms, messageProvider);

        // 更新发送结果
        if (SUCCESS_CODE.equals(result.getCode())) {
          sms.setStatus(SmsStatus.SENT);
          sms.setMessageId(result.getMessage());
        } else {
          sms.setStatus(SmsStatus.FAILED);
          sms.setErrorCode(result.getCode());
          sms.setErrorMessage(result.getMessage());
        }
        return smsRepo.save(sms);
      }
    }.execute();
  }

}
