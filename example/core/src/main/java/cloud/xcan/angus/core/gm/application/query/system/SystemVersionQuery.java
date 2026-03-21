package cloud.xcan.angus.core.gm.application.query.system;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.domain.system.SystemVersion;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface SystemVersionQuery {

  /**
   * 根据ID查找系统版本
   */
  Optional<SystemVersion> findById(Long id);

  /**
   * 查找当前版本信息
   */
  Optional<SystemVersion> findCurrent();

  /**
   * 根据应用编码和版本号查找系统版本
   */
  Optional<SystemVersion> findVersion(String appCode, String fromVersion);

  /**
   * 分页查询系统版本列表
   */
  Page<SystemVersion> findVersions(GenericSpecification<SystemVersion> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 根据应用编码和版本类型获取版本信息
   */
  List<SystemVersion> findVersion(String appCode, EditionType editionType);

  /**
   * 获取最新版本
   */
  Optional<SystemVersion> findLatestVersion(String appCode, EditionType editionType);

}
