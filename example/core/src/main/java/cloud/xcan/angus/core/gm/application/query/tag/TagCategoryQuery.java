package cloud.xcan.angus.core.gm.application.query.tag;

import cloud.xcan.angus.core.gm.domain.tag.TagCategory;
import java.util.List;

/**
 * 标签分类查询服务接口
 */
public interface TagCategoryQuery {

  /**
   * 根据ID查询并检查存在性
   */
  TagCategory findAndCheck(Long id);

  /**
   * 查询标签分类列表
   */
  List<TagCategory> findAll();

  /**
   * 获取分类下的标签数量
   */
  Integer getTagCount(Long categoryId);

  /**
   * 根据编码查询分类并检查存在性
   */
  TagCategory findByCodeAndCheck(String code);

}
