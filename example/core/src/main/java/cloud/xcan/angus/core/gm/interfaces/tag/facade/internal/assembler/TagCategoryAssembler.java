package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.core.gm.domain.tag.TagCategory;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.CreateTagCategoryDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.UpdateTagCategoryDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagCategoryVo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 标签分类数据组装器
 */
public class TagCategoryAssembler {

  public static TagCategory toCreateDomain(CreateTagCategoryDto dto) {
    TagCategory category = new TagCategory();
    category.setCode(dto.getCode());
    category.setName(dto.getName());
    category.setDescription(dto.getDescription());
    category.setIsSystem(false);
    return category;
  }

  public static TagCategory toUpdateDomain(Long id, UpdateTagCategoryDto dto) {
    TagCategory category = new TagCategory();
    category.setId(id);
    category.setName(dto.getName());
    category.setDescription(dto.getDescription());
    return category;
  }

  public static TagCategoryVo toVo(TagCategory category) {
    if (category == null) {
      return null;
    }
    TagCategoryVo vo = new TagCategoryVo();
    vo.setId(category.getId());
    vo.setCode(category.getCode());
    vo.setName(category.getName());
    vo.setDescription(category.getDescription());
    vo.setIsSystem(category.getIsSystem());
    vo.setTagCount(nullSafe(category.getTagCount(), 0));

    // 设置审计字段
    vo.setCreatedBy(category.getCreatedBy());
    vo.setCreatedDate(category.getCreatedDate());
    vo.setModifiedBy(category.getModifiedBy());
    vo.setModifiedDate(category.getModifiedDate());
    return vo;
  }

  public static List<TagCategoryVo> toVoList(List<TagCategory> categories) {
    return categories.stream()
        .map(TagCategoryAssembler::toVo)
        .collect(Collectors.toList());
  }
}
