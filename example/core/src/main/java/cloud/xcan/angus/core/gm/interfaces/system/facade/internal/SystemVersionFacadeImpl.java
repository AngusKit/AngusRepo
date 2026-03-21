package cloud.xcan.angus.core.gm.interfaces.system.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.application.query.system.SystemVersionQuery;
import cloud.xcan.angus.core.gm.domain.system.SystemVersion;
import cloud.xcan.angus.core.gm.interfaces.system.facade.SystemVersionFacade;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.ChangelogFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.VersionHistoryFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.internal.assembler.SystemVersionAssembler;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.ChangelogVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CurrentVersionVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.UpdateCheckVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionCompareVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionDetailVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionHistoryVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SystemVersionFacadeImpl implements SystemVersionFacade {

  @Resource
  private SystemVersionQuery systemVersionQuery;

  @Override
  public CurrentVersionVo getCurrentVersion() {
    SystemVersion currentVersion = systemVersionQuery.findCurrent()
        .orElseThrow(() -> ResourceNotFound.of("未找到当前应用版本信息", new Object[]{}));
    return SystemVersionAssembler.toCurrentVersionVo(currentVersion);
  }

  @Override
  public List<CurrentVersionVo> getApplicationVersions(String appCode, EditionType editionType) {
    List<SystemVersion> versions
        = systemVersionQuery.findVersion(appCode, editionType);
    return versions.stream().map(SystemVersionAssembler::toCurrentVersionVo)
        .collect(Collectors.toList());
  }

  @Override
  public PageResult<VersionHistoryVo> listVersionHistory(VersionHistoryFindDto dto) {
    GenericSpecification<SystemVersion> spec = SystemVersionAssembler
        .getVersionHistorySpecification(dto);
    Page<SystemVersion> page = systemVersionQuery.findVersions(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, SystemVersionAssembler::toVersionHistoryVo);
  }

  @Override
  public VersionDetailVo getVersionDetail(String id) {
    SystemVersion version = systemVersionQuery.findById(Long.parseLong(id))
        .orElseThrow(() -> ResourceNotFound.of("系统版本「{0}」不存在", new Object[]{id}));
    return SystemVersionAssembler.toVersionDetailVo(version);
  }

  @Override
  public PageResult<ChangelogVo> getChangelog(ChangelogFindDto dto) {
    GenericSpecification<SystemVersion> spec
        = SystemVersionAssembler.getChangelogSpecification(dto);
    Page<SystemVersion> page = systemVersionQuery.findVersions(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, SystemVersionAssembler::toChangelogVo);
  }

  @Override
  public UpdateCheckVo checkUpdate(String appCode, EditionType editionType) {
    SystemVersion currentVersion = systemVersionQuery.findCurrent()
        .orElseThrow(() -> ResourceNotFound.of("未找到当前应用版本信息", new Object[]{}));
    SystemVersion latestVersion = systemVersionQuery.findLatestVersion(appCode, editionType)
        .orElse(null);
    if (latestVersion == null) {
      UpdateCheckVo vo = new UpdateCheckVo();
      vo.setHasUpdate(false);
      return vo;
    }
    return SystemVersionAssembler.toUpdateCheckVo(currentVersion, latestVersion);
  }

  @Override
  public VersionCompareVo compareVersions(String appCode, String fromVersion, String toVersion) {
    SystemVersion from = systemVersionQuery.findVersion(appCode, fromVersion)
        .orElseThrow(() -> ResourceNotFound.of("起始版本「{0}」不存在", new Object[]{fromVersion}));
    SystemVersion to = systemVersionQuery.findVersion(appCode, toVersion)
        .orElseThrow(() -> ResourceNotFound.of("目标版本「{0}」不存在", new Object[]{toVersion}));
    return SystemVersionAssembler.toVersionCompareVo(from, to);
  }

}
