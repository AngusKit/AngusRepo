package cloud.xcan.angus.core.gm.application.query.interfaces;

import cloud.xcan.angus.api.commonlink.EnabledStatus;
import cloud.xcan.angus.core.gm.domain.interfaces.Interface;
import cloud.xcan.angus.core.gm.domain.interfaces.TagCount;
import cloud.xcan.angus.core.gm.domain.interfaces.enums.InterfaceSyncAction;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 接口查询服务接口 用于查询接口信息、统计接口数量等操作
 */
public interface InterfaceQuery {

  /**
   * 根据ID查找接口并检查是否存在
   */
  Interface findAndCheck(Long id);

  /**
   * 分页查询接口
   */
  Page<Interface> find(GenericSpecification<Interface> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 根据服务名称查询接口
   */
  Page<Interface> findByServiceName(String serviceName, GenericSpecification<Interface> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match);

  /**
   * 根据标签查询接口
   */
  Page<Interface> findByTag(String tag, GenericSpecification<Interface> spec,
      PageRequest pageable, boolean fullTextSearch, String[] match);

  /**
   * 统计接口总数
   */
  long countTotal();

  /**
   * 根据状态统计接口数量
   */
  long countByStatus(EnabledStatus status);

  /**
   * 根据服务名称统计接口数量
   */
  long countByServiceName(String serviceName);

  /**
   * 根据服务名称和废弃状态统计接口数量
   */
  long countByServiceNameAndDeprecated(String serviceName, Boolean deprecated);

  /**
   * 根据服务名称和同步操作类型统计接口数量
   */
  long countByServiceNameAndLastSyncAction(String serviceName, InterfaceSyncAction lastSyncAction);

  /**
   * 获取所有不重复的服务名称列表
   */
  List<String> findDistinctServiceNames();

  /**
   * 根据标签分组统计接口数量 返回每个标签及其对应的接口数量，用于性能优化
   */
  List<TagCount> countGroupByTag();
}
