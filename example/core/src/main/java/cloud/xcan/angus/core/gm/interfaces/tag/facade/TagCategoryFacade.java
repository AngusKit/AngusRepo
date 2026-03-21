package cloud.xcan.angus.core.gm.interfaces.tag.facade;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.CreateTagCategoryDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.UpdateTagCategoryDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagCategoryVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import java.util.List;

/**
 * 标签分类门面接口
 */
public interface TagCategoryFacade {

  /**
   * 创建标签分类
   */
  TagCategoryVo create(CreateTagCategoryDto dto);

  /**
   * 更新标签分类
   */
  TagCategoryVo update(Long id, UpdateTagCategoryDto dto);

  /**
   * 删除标签分类
   */
  void delete(Long id);

  /**
   * 查询标签分类详情
   */
  TagCategoryVo getById(Long id);

  /**
   * 查询标签分类列表
   */
  List<TagCategoryVo> list();

  /**
   * 根据分类编码查询该分类下的所有标签列表
   */
  List<TagListVo> getTagListByCategoryCode(String code);
}
