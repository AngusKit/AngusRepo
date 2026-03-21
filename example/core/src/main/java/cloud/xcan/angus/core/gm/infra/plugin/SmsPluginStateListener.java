package cloud.xcan.angus.core.gm.infra.plugin;


import static cloud.xcan.angus.core.spring.SpringContextHolder.getCachedUidGenerator;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsProviderCmd;
import cloud.xcan.angus.core.gm.domain.sms.SmsProvider;
import cloud.xcan.angus.core.gm.domain.sms.SmsProviderRepo;
import cloud.xcan.angus.core.spring.SpringContextHolder;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.extension.sms.api.MessageProvider;
import cloud.xcan.angus.extension.sms.api.SmsProviderExtension;
import cloud.xcan.angus.plugin.core.PluginState;
import cloud.xcan.angus.plugin.core.PluginStateEvent;
import cloud.xcan.angus.plugin.core.PluginStateListener;
import cloud.xcan.angus.plugin.core.PluginWrapper;
import cloud.xcan.angus.plugin.spring.SpringPluginManager;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@DependsOn("applicationContextProvider")
public class SmsPluginStateListener implements PluginStateListener {

  @Resource
  private ApplicationInfo applicationInfo;

  @Resource
  private SmsProviderRepo smsProviderRepo;

  @Resource
  private SmsProviderCmd smsProviderCmd;

  /**
   * 已处理的插件ID集合
   */
  private final List<String> pluginIds = new ArrayList<>();

  /**
   * 所有插件的所有扩展通道，通道名称不能重复
   */
  private final List<MessageProvider> pluginProviders = new ArrayList<>();

  @Override
  public void pluginStateChanged(PluginStateEvent pluginStateEvent) {
    try {
      if (PluginState.STARTED.equals(pluginStateEvent.getPluginState())) {
        String pluginId = pluginStateEvent.getPlugin().getPluginId();
        if (!pluginIds.contains(pluginId)) {
          pluginIds.add(pluginStateEvent.getPlugin().getPluginId());
          // 获取插件的所有通道
          List<SmsProviderExtension> loadProviders = pluginStateEvent.getPlugin()
              .getPluginManager().getExtensions(SmsProviderExtension.class, pluginId);
          if (isEmpty(loadProviders)) {
            log.error("插件 [{}] 未找到 SmsProviderExtension 扩展", pluginId);
            return;
          }

          // 过滤重复的通道
          loadProviders.forEach(provider -> {
            if (!pluginProviders.stream().map(MessageProvider::getName)
                .collect(Collectors.toSet())
                .contains(provider.getInstallationProvider().getName())) {
              pluginProviders.add(provider.getInstallationProvider());
            }
          });

          if (isEmpty(pluginProviders)) {
            log.error("插件 [{}] SmsProviderExtension 安装通道为空", pluginId);
            return;
          }

          // 获取插件通道信息并转换为领域通道
          List<SmsProvider> smsProviders = pluginProviders.stream()
              .map(channel -> toInitInstallChannel(channel, applicationInfo))
              .collect(Collectors.toList());

          // 获取数据库中已有的通道信息
          List<SmsProvider> smsProviderDbs = smsProviderRepo.findAll();
          Map<String, SmsProvider> smsProviderDbMap = smsProviderDbs.stream().collect(
              Collectors.toMap(SmsProvider::getName, o -> o));

          // 新增或更新通道信息
          smsProviders.forEach(provider -> {
            if (!smsProviderDbMap.containsKey(provider.getName())) {
              // 新服务商：设置默认状态和ID
              provider.setStatus(EnabledStatus.ENABLED);
              provider.setIsDefault(false);
              provider.setId(getCachedUidGenerator().getUID());
            } else {
              // 已存在的服务商：保留数据库配置（状态、是否默认、配置等），仅更新插件提供的信息（logo、name等）
              SmsProvider providerDb = smsProviderDbMap.get(provider.getName());
              // 从插件信息复制到数据库对象，但保留数据库的关键配置
              provider.setId(providerDb.getId());
              provider.setStatus(providerDb.getStatus());
              provider.setIsDefault(providerDb.getIsDefault());
              provider.setConfig(providerDb.getConfig());
            }
          });
          smsProviderCmd.replace(smsProviders);
        }
      }
    } catch (Exception e) {
      // 获取插件通道失败，记录异常事件
      log.error("获取插件错误:", e);
    }
  }

  /**
   * 获取插件通道信息并转换为领域通道
   */
  public static SmsProvider toInitInstallChannel(
      MessageProvider pluginProvider, ApplicationInfo applicationInfo) {
    if (StringUtils.equals(EditionType.CLOUD_SERVICE.name(), applicationInfo.getEditionType())) {
      Map<String, String> config = new HashMap<>();
      config.put("endpoint", pluginProvider.getEndpoint());
      config.put("accessKeyId", pluginProvider.getAccessKeyId());
      config.put("accessKeySecret", pluginProvider.getAccessKeySecret());
      config.put("thirdChannelNo", pluginProvider.getThirdChannelNo());
      return new SmsProvider()
          .setLogo(pluginProvider.getLogo())
          .setName(pluginProvider.getName())
          .setConfig(config);
    }

    // 私有化版本需要租户自行配置短信通道信息
    return new SmsProvider()
        .setLogo(pluginProvider.getLogo()).setName(pluginProvider.getName());
  }

  /**
   * 根据服务商名称获取插件实例
   */
  public static SmsProviderExtension getPluginProvider(
      String providerName) {
    try {
      SpringPluginManager pluginManager = SpringContextHolder.getBean(SpringPluginManager.class);
      if (pluginManager == null) {
        log.error("未找到 SpringPluginManager 实例");
        return null;
      }

      // 遍历所有插件，查找匹配的 SmsProviderExtension
      List<PluginWrapper> plugins = pluginManager.getPlugins();
      for (PluginWrapper plugin : plugins) {
        if (plugin.getPluginState().equals(PluginState.STARTED)) {
          List<SmsProviderExtension> providers = plugin.getPluginManager()
              .getExtensions(SmsProviderExtension.class,
                  plugin.getPluginId());
          for (SmsProviderExtension provider : providers) {
            if (provider != null && provider.getInstallationProvider() != null) {
              if (providerName.equals(provider.getInstallationProvider().getName())) {
                return provider;
              }
            }
          }
        }
      }
    } catch (Exception e) {
      log.error("获取短信插件实例失败: {}", providerName, e);
    }
    return null;
  }

  /**
   * 将领域 SmsProviderExtension 转换为 MessageProvider
   */
  public static MessageProvider toMessageProvider(
      SmsProvider provider) {
    MessageProvider messageProvider = new MessageProvider();
    messageProvider.setName(provider.getName());
    messageProvider.setLogo(provider.getLogo());

    // 从配置中获取服务商参数
    if (isNotEmpty(provider.getConfig())) {
      Map<String, String> config = provider.getConfig();
      messageProvider.setEndpoint(config.get("endpoint"));
      messageProvider.setAccessKeyId(config.get("accessKeyId"));
      messageProvider.setAccessKeySecret(config.get("accessKeySecret"));
      messageProvider.setThirdChannelNo(config.get("thirdChannelNo"));
    }
    return messageProvider;
  }

}
