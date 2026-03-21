package cloud.xcan.angus.core.gm.application.cmd.sms.impl;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsProviderCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsProviderQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.sms.SmsProvider;
import cloud.xcan.angus.core.gm.domain.sms.SmsProviderRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SmsProviderCmdImpl extends CommCmd<SmsProvider, Long> implements SmsProviderCmd {

  @Resource
  private SmsProviderRepo smsProviderRepo;

  @Resource
  private SmsProviderQuery smsProviderQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void replace(List<SmsProvider> smsProviders) {
    new BizTemplate<Void>() {
      @Override
      protected Void process() {
        // 批量保存或更新短信服务商（插件自动触发，无需权限检查）
        // 传入的服务商列表已经处理好了：
        // - 新服务商：已设置ID、status、isDefault
        // - 已存在的服务商：已设置ID并保留了数据库的关键配置
        if (smsProviders != null && !smsProviders.isEmpty()) {
          smsProviderRepo.saveAll(smsProviders);
        }
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SmsProvider updateProvider(SmsProvider provider) {
    return new BizTemplate<SmsProvider>() {
      SmsProvider existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        existing = smsProviderQuery.findAndCheck(provider.getId());
      }

      @Override
      protected SmsProvider process() {
        // 如果设置为默认服务商，取消其他默认服务商
        if (Boolean.TRUE.equals(provider.getIsDefault())) {
          smsProviderRepo.findByIsDefaultTrue()
              .filter(p -> !p.getId().equals(provider.getId()))
              .ifPresent(provider -> {
                provider.setIsDefault(false);
                smsProviderRepo.save(provider);
              });
        }

        // 更新字段
        CoreUtils.copyPropertiesIgnoreNull(provider, existing);
        SmsProvider saved = smsProviderRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.SMS_PROVIDER_UPDATE_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SmsProvider updateProviderStatus(Long id, EnabledStatus status) {
    return new BizTemplate<SmsProvider>() {
      SmsProvider existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        existing = smsProviderQuery.findAndCheck(id);
      }

      @Override
      protected SmsProvider process() {
        existing.setStatus(status);
        SmsProvider saved = smsProviderRepo.save(existing);

        // 记录操作日志
        String statusText = status.isEnabled() ? "YES" : "NO";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.SMS_PROVIDER_UPDATE_STATUS_DETAILS,
            new Object[]{saved.getName(), statusText}
        );
        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public SmsProvider setDefaultProvider(Long id) {
    return new BizTemplate<SmsProvider>() {
      SmsProvider existing;

      @Override
      protected void checkParams() {
        PermissionCheck.checkCloudTenantSecurity();
        existing = smsProviderQuery.findAndCheck(id);

        if (!existing.getStatus().isEnabled()) {
          throw ProtocolException.of("服务商「{0}」未启用，无法设置为默认服务商",
              new Object[]{existing.getName()});
        }
      }

      @Override
      protected SmsProvider process() {
        // 取消其他默认服务商
        smsProviderRepo.findByIsDefaultTrue()
            .filter(p -> !p.getId().equals(id))
            .ifPresent(provider -> {
              provider.setIsDefault(false);
              smsProviderRepo.save(provider);
            });

        // 设置当前为默认
        existing.setIsDefault(true);
        SmsProvider saved = smsProviderRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.CONFIG,
            saved.getId(),
            saved.getName(),
            OperationMessage.SMS_PROVIDER_SET_DEFAULT_DETAILS,
            new Object[]{saved.getName()}
        );
        return saved;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<SmsProvider, Long> getRepository() {
    return smsProviderRepo;
  }
}
