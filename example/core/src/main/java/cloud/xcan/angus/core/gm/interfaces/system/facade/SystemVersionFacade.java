package cloud.xcan.angus.core.gm.interfaces.system.facade;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.ChangelogFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.dto.VersionHistoryFindDto;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.ChangelogVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.CurrentVersionVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.UpdateCheckVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionCompareVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionDetailVo;
import cloud.xcan.angus.core.gm.interfaces.system.facade.vo.VersionHistoryVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface SystemVersionFacade {

  /**
   * 获取当前系统最新版本信息
   */
  CurrentVersionVo getCurrentVersion();

  /**
   * 获取指定应用的所有版本信息
   */
  List<CurrentVersionVo> getApplicationVersions(String appCode, EditionType editionType);

  /**
   * 获取版本历史列表
   */
  PageResult<VersionHistoryVo> listVersionHistory(VersionHistoryFindDto dto);

  /**
   * 获取版本详情
   */
  VersionDetailVo getVersionDetail(String id);

  /**
   * 获取变更日志
   */
  PageResult<ChangelogVo> getChangelog(ChangelogFindDto dto);

  /**
   * 检查更新
   */
  UpdateCheckVo checkUpdate(String appCode, EditionType editionType);

  /**
   * 获取版本对比
   */
  VersionCompareVo compareVersions(String appCode, String fromVersion, String toVersion);

}
