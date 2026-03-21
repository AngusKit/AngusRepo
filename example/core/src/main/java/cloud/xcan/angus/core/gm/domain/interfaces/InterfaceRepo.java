package cloud.xcan.angus.core.gm.domain.interfaces;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceSyncAction;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface InterfaceRepo extends BaseRepository<Interface, Long> {

  /**
   * 根据编码列表查找接口
   */
  List<Interface> findByCodeIn(Collection<String> codes);

  /**
   * 检查编码是否存在
   */
  boolean existsByCode(String code);

  /**
   * 根据状态统计接口数量
   */
  long countByStatus(EnabledStatus status);

  /**
   * 根据服务名称统计接口数量
   */
  long countByServiceName(String serviceName);

  /**
   * 根据服务名称统计废弃接口数量
   */
  long countByServiceNameAndDeprecated(String serviceName, Boolean deprecated);

  /**
   * 根据服务名称和同步操作类型统计接口数量
   */
  long countByServiceNameAndLastSyncAction(String serviceName, InterfaceSyncAction lastSyncAction);

  /**
   * 查找所有不重复的服务名称
   */
  @Query("SELECT DISTINCT i.serviceName FROM Interface i")
  List<String> findDistinctServiceNames();

  /**
   * 根据标签分组统计接口数量 返回每个标签及其对应的接口数量
   */
  @Query(value = "SELECT tag AS tag, COUNT(*) AS count FROM gm_interfaces WHERE tag IS NOT NULL AND tag != '' GROUP BY tag ORDER BY tag", nativeQuery = true)
  List<TagCount> countGroupByTag();
}
