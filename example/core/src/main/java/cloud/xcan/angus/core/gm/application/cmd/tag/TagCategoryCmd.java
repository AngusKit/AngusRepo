package cloud.xcan.angus.core.gm.application.cmd.tag;

import cloud.xcan.angus.core.gm.domain.tag.TagCategory;

/**
 * 标签分类命令服务接口
 */
public interface TagCategoryCmd {

  /**
   * 创建标签分类
   */
  TagCategory create(TagCategory category);

  /**
   * 更新标签分类（仅允许更新非系统分类）
   */
  TagCategory update(TagCategory category);

  /**
   * 删除标签分类（仅允许删除非系统分类且没有标签的分类）
   */
  void delete(Long id);
}
