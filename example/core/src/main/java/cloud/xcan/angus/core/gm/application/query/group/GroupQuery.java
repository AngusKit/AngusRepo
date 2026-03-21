package cloud.xcan.angus.core.gm.application.query.group;

import cloud.xcan.angus.api.commonlink.group.Group;
import cloud.xcan.angus.api.commonlink.group.enums.GroupType;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface GroupQuery {

  /**
   * 根据ID查找组并检查存在性
   */
  Group findAndCheck(Long id);

  /**
   * 分页查询组列表
   */
  Page<Group> find(GenericSpecification<Group> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match);

  /**
   * 根据用户ID查找组列表
   */
  List<Group> findByUserId(Long userId);

  /**
   * 统计本月新增组数量
   */
  long countNewGroupsThisMonth();

  /**
   * 检查编码是否存在
   */
  boolean existsByCode(String code);

  /**
   * 统计所有组数量
   */
  long count();

  /**
   * 按类型统计组数量
   */
  long countByType(GroupType type);

  void setOwnerUser(List<Group> groups);

  /**
   * 根据ID列表批量查找组
   */
  List<Group> findAllById(List<Long> ids);
}
