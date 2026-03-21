package cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler;

import static cloud.xcan.angus.core.gm.infra.utils.CommonUtils.formatUptime;

import cloud.xcan.angus.core.gm.domain.system.SystemVersion;
import cloud.xcan.angus.core.gm.domain.system.model.VersionBreakingChange;
import cloud.xcan.angus.core.gm.domain.system.model.VersionBugFix;
import cloud.xcan.angus.core.gm.domain.system.model.VersionFeature;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.ChangelogFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.VersionHistoryFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.ChangelogVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CurrentVersionVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.UpdateCheckVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionCompareVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionCompareVo.FeatureChanges;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionCompareVo.FeatureItem;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionDetailVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionHistoryVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import oshi.SystemInfo;
import oshi.software.os.OperatingSystem;


public class SystemVersionAssembler {

  public static VersionHistoryVo toVersionHistoryVo(SystemVersion version) {
    VersionHistoryVo vo = new VersionHistoryVo();
    vo.setId(version.getId());
    vo.setVersion(version.getVersion());
    vo.setTitle(version.getVersion());
    vo.setDescription(version.getDescription());
    vo.setReleaseDate(version.getReleaseDate());
    vo.setReleaseType(version.getType());
    vo.setAppCode(version.getAppCode());
    vo.setEditionType(version.getEditionType());
    return vo;
  }

  public static VersionDetailVo toVersionDetailVo(SystemVersion version) {
    VersionDetailVo vo = new VersionDetailVo();
    vo.setId(version.getId() != null ? version.getId().toString() : version.getVersion());
    vo.setVersion(version.getVersion());
    vo.setTitle(version.getVersion());
    vo.setDescription(version.getDescription());
    vo.setReleaseDate(version.getReleaseDate());
    vo.setReleaseType(version.getType() != null ? version.getType().name() : null);

    // 转换 features
    if (version.getFeatures() != null) {
      List<VersionDetailVo.FeatureItem> featureItems = version.getFeatures().stream()
          .map(feature -> {
            VersionDetailVo.FeatureItem item = new VersionDetailVo.FeatureItem();
            item.setType(feature.getType());
            item.setModule(feature.getModule());
            item.setDescription(feature.getDescription());
            return item;
          })
          .collect(Collectors.toList());
      vo.setFeatures(featureItems);
    }
    // 转换 breakingChanges
    if (version.getBreakingChanges() != null) {
      List<String> breakingChangeList = version.getBreakingChanges().stream()
          .map(VersionBreakingChange::getDescription)
          .collect(Collectors.toList());
      vo.setBreakingChanges(breakingChangeList);
    }
    return vo;
  }

  public static ChangelogVo toChangelogVo(SystemVersion version) {
    ChangelogVo vo = new ChangelogVo();
    vo.setVersion(version.getVersion());
    vo.setReleaseDate(version.getReleaseDate());

    // 合并 features 和 bugFixes 为 changes
    List<ChangelogVo.ChangeItem> changes = new ArrayList<>();
    if (version.getFeatures() != null) {
      for (VersionFeature feature : version.getFeatures()) {
        ChangelogVo.ChangeItem item = new ChangelogVo.ChangeItem();
        item.setType(feature.getType());
        item.setModule(feature.getModule());
        item.setDescription(feature.getDescription());
        item.setIssueId(feature.getIssueId());
        changes.add(item);
      }
    }
    if (version.getBugFixes() != null) {
      for (VersionBugFix bugFix : version.getBugFixes()) {
        ChangelogVo.ChangeItem item = new ChangelogVo.ChangeItem();
        item.setType(bugFix.getType());
        item.setModule(bugFix.getModule());
        item.setDescription(bugFix.getDescription());
        item.setIssueId(bugFix.getIssueId());
        changes.add(item);
      }
    }
    vo.setChanges(changes);

    return vo;
  }

  public static CurrentVersionVo toCurrentVersionVo(SystemVersion version) {
    CurrentVersionVo vo = new CurrentVersionVo();
    vo.setVersion(version.getVersion());
    vo.setReleaseDate(version.getReleaseDate());
    vo.setAppCode(version.getAppCode());
    vo.setEditionType(version.getEditionType());

    // 构建号：从版本号中提取，或使用版本号本身
    vo.setBuildNumber(version.getVersion());

    // 运行环境：从 Spring profile 获取
    String activeProfile = System.getProperty("spring.profiles.active");
    if (activeProfile == null || activeProfile.isEmpty()) {
      activeProfile = System.getenv("SPRING_PROFILES_ACTIVE");
    }
    if (activeProfile == null || activeProfile.isEmpty()) {
      activeProfile = "production";
    } else {
      // 如果有多个 profile，取第一个
      String[] profiles = activeProfile.split(",");
      activeProfile = profiles.length > 0 ? profiles[0].trim() : "production";
    }
    vo.setEnvironment(activeProfile);

    // 组件信息：使用系统属性构建基本信息
    Map<String, Object> components = new HashMap<>();
    components.put("java.version", System.getProperty("java.version"));
    components.put("java.vendor", System.getProperty("java.vendor"));
    components.put("os.name", System.getProperty("os.name"));
    components.put("os.version", System.getProperty("os.version"));
    components.put("os.arch", System.getProperty("os.arch"));
    vo.setComponents(components);

    // 运行时长和启动时间：使用 OSHI 获取系统运行时间
    try {
      SystemInfo systemInfo = new SystemInfo();
      OperatingSystem os = systemInfo.getOperatingSystem();
      long uptimeSeconds = os.getSystemUptime();
      vo.setUptime(formatUptime(uptimeSeconds));
      vo.setStartTime(LocalDateTime.now().minusSeconds(uptimeSeconds));
    } catch (Exception e) {
      // 如果获取失败，设置为 null
      vo.setUptime(null);
      vo.setStartTime(null);
    }

    if (version.getFeatures() != null) {
      vo.setFeatures(version.getFeatures().stream()
          .map(x -> new VersionDetailVo.FeatureItem()
              .setType(x.getType()).setModule(x.getModule()).setDescription(x.getDescription()))
          .collect(Collectors.toList()));
    }
    return vo;
  }

  public static UpdateCheckVo toUpdateCheckVo(SystemVersion currentVersion,
      SystemVersion latestVersion) {
    UpdateCheckVo vo = new UpdateCheckVo();
    vo.setCurrentVersion(currentVersion.getVersion());
    if (latestVersion != null) {
      vo.setLatestVersion(latestVersion.getVersion());
      vo.setHasUpdate(!currentVersion.getVersion().equals(latestVersion.getVersion()));

      // 设置最新版本的详细信息
      if (latestVersion.getReleaseDate() != null) {
        vo.setReleaseDate(
            latestVersion.getReleaseDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
      }
      if (latestVersion.getType() != null) {
        vo.setReleaseType(latestVersion.getType().name().toLowerCase());
      }
      vo.setTitle(latestVersion.getVersion());
      vo.setDescription(latestVersion.getDescription());

      // 检查是否有破坏性变更
      boolean hasBreakingChanges = latestVersion.getBreakingChanges() != null
          && !latestVersion.getBreakingChanges().isEmpty();
      vo.setBreakingChanges(hasBreakingChanges);
      vo.setRequiresDowntime(hasBreakingChanges);

      // 下载地址和发布说明（可以根据实际需求配置）
      // vo.setDownloadUrl("https://releases.angusgm.com/v" + latestVersion.getVersion() + "/angusgm-" + latestVersion.getVersion() + ".tar.gz");
      // vo.setReleaseNotes("https://docs.angusgm.com/releases/v" + latestVersion.getVersion());
    } else {
      vo.setHasUpdate(false);
    }
    return vo;
  }

  public static VersionCompareVo toVersionCompareVo(SystemVersion fromVersion,
      SystemVersion toVersion) {
    VersionCompareVo vo = new VersionCompareVo();
    vo.setFromVersion(fromVersion.getVersion());
    vo.setToVersion(toVersion.getVersion());

    // 比较功能变更
    FeatureChanges featureChanges = new FeatureChanges();
    featureChanges.setAdded(new ArrayList<>());
    featureChanges.setImproved(new ArrayList<>());
    featureChanges.setFixed(new ArrayList<>());
    featureChanges.setRemoved(new ArrayList<>());

    // 获取目标版本的 features
    Map<String, VersionFeature> toFeaturesMap = new HashMap<>();
    if (toVersion.getFeatures() != null) {
      for (VersionFeature feature : toVersion.getFeatures()) {
        String key = feature.getModule() + ":" + feature.getDescription();
        toFeaturesMap.put(key, feature);
      }
    }

    // 获取起始版本的 features
    Map<String, VersionFeature> fromFeaturesMap = new HashMap<>();
    if (fromVersion.getFeatures() != null) {
      for (VersionFeature feature : fromVersion.getFeatures()) {
        String key = feature.getModule() + ":" + feature.getDescription();
        fromFeaturesMap.put(key, feature);
      }
    }

    // 找出新增和改进的功能
    for (VersionFeature toFeature : toFeaturesMap.values()) {
      String key = toFeature.getModule() + ":" + toFeature.getDescription();
      VersionFeature fromFeature = fromFeaturesMap.get(key);
      if (fromFeature == null) {
        // 新增功能
        FeatureItem item = new FeatureItem();
        item.setModule(toFeature.getModule());
        item.setDescription(toFeature.getDescription());
        featureChanges.getAdded().add(item);
      } else if (!fromFeature.getType().equals(toFeature.getType())) {
        // 改进功能
        FeatureItem item = new FeatureItem();
        item.setModule(toFeature.getModule());
        item.setDescription(toFeature.getDescription());
        featureChanges.getImproved().add(item);
      }
    }

    // 找出移除的功能
    for (VersionFeature fromFeature : fromFeaturesMap.values()) {
      String key = fromFeature.getModule() + ":" + fromFeature.getDescription();
      if (!toFeaturesMap.containsKey(key)) {
        FeatureItem item = new FeatureItem();
        item.setModule(fromFeature.getModule());
        item.setDescription(fromFeature.getDescription());
        featureChanges.getRemoved().add(item);
      }
    }

    // 获取修复的问题
    if (toVersion.getBugFixes() != null) {
      for (VersionBugFix bugFix : toVersion.getBugFixes()) {
        FeatureItem item = new FeatureItem();
        item.setModule(bugFix.getModule());
        item.setDescription(bugFix.getDescription());
        featureChanges.getFixed().add(item);
      }
    }

    vo.setFeatures(featureChanges);

    // 破坏性变更
    List<String> breakingChanges = new ArrayList<>();
    if (toVersion.getBreakingChanges() != null) {
      for (VersionBreakingChange breakingChange : toVersion.getBreakingChanges()) {
        breakingChanges.add(breakingChange.getDescription());
      }
    }
    vo.setBreakingChanges(breakingChanges);

    // 迁移信息
    List<String> migrations = new ArrayList<>();
    if (toVersion.getBreakingChanges() != null) {
      for (VersionBreakingChange breakingChange : toVersion.getBreakingChanges()) {
        if (breakingChange.getMigrationGuide() != null
            && !breakingChange.getMigrationGuide().isEmpty()) {
          migrations.add(breakingChange.getMigrationGuide());
        }
      }
    }
    vo.setMigrations(migrations);

    return vo;
  }

  public static GenericSpecification<SystemVersion> getChangelogSpecification(
      ChangelogFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "releaseDate")
        .orderByFields("releaseDate", "version")
        .matchSearchFields("changelog", "description")
        .build();
    return new GenericSpecification<>(filters);
  }

  public static GenericSpecification<SystemVersion> getVersionHistorySpecification(
      VersionHistoryFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "releaseDate", "createdDate")
        .orderByFields("id", "releaseDate", "createdDate", "version")
        .matchSearchFields("version", "appCode", "description")
        .build();
    return new GenericSpecification<>(filters);
  }

}
