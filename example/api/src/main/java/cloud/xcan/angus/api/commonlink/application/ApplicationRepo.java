package cloud.xcan.angus.api.commonlink.application;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationSource;
import cloud.xcan.angus.api.commonlink.application.enums.ApplicationType;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.jpa.repository.NameJoinRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository("commonApplicationRepo")
public interface ApplicationRepo extends NameJoinRepository<Application, Long>,
    BaseRepository<Application, Long> {

  /**
   * 根据客户端ID查找应用
   */
  Optional<Application> findByClientId(String clientId);

  /**
   * 根据编码查找应用（包含所有版本类型）
   */
  List<Application> findByCode(String code);

  /**
   * 根据编码和版本类型查找应用
   */
  List<Application> findByCodeAndEditionType(String code, EditionType editionType);

  /**
   * 检查编码是否存在
   */
  boolean existsByCode(String code);

  /**
   * 根据状态分页查找应用
   */
  Page<Application> findByStatus(EnabledStatus status, Pageable pageable);

  /**
   * 根据类型分页查找应用
   */
  Page<Application> findByType(ApplicationType type, Pageable pageable);

  /**
   * 根据来源查找应用列表
   */
  List<Application> findBySource(ApplicationSource source);

  /**
   * 根据状态统计应用数量
   */
  long countByStatus(EnabledStatus status);

}
