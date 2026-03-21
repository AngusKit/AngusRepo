package cloud.xcan.angus.core.gm.domain.system;

import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.gm.domain.system.enums.VersionStatus;
import cloud.xcan.angus.core.gm.domain.system.enums.VersionType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SystemVersionRepo extends JpaRepository<SystemVersion, Long>,
    JpaSpecificationExecutor<SystemVersion> {

  /**
   * 根据应用编码和版本号查找
   */
  Optional<SystemVersion> findByAppCodeAndVersionAndEditionType(String appCode, String version,
      EditionType editionType);

  /**
   * 根据类型查找
   */
  List<SystemVersion> findByType(VersionType type);

  /**
   * 根据状态查找
   */
  Page<SystemVersion> findByStatus(VersionStatus status, Pageable pageable);

  /**
   * 根据应用编码和版本类型查找版本
   */
  List<SystemVersion> findTopByAppCodeAndEditionType(String appCode, EditionType editionType);

  /**
   * 根据应用编码和版本类型查找最新版本
   */
  Optional<SystemVersion> findTop1ByAppCodeAndEditionTypeOrderByReleaseDateDesc(String appCode,
      EditionType editionType);
}
