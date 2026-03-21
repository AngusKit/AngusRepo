package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagCategory;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.Map;
import java.util.Set;

public class TagAssembler {

  public static Tag toCreateDomain(TagCreateDto dto) {
    Tag tag = new Tag();
    tag.setName(dto.getName());
    tag.setDescription(dto.getDescription());
    tag.setCategoryId(dto.getCategoryId());
    tag.setIsSystem(false);
    return tag;
  }

  public static Tag toUpdateDomain(Long id, TagUpdateDto dto) {
    Tag tag = new Tag();
    tag.setId(id);
    tag.setName(dto.getName());
    tag.setDescription(dto.getDescription());
    tag.setCategoryId(dto.getCategoryId());
    return tag;
  }

  public static TagDetailVo toDetailVo(Tag tag, Map<Long, TagCategory> categoryMap) {
    TagDetailVo vo = new TagDetailVo();
    vo.setId(tag.getId());
    vo.setName(tag.getName());
    vo.setDescription(tag.getDescription());
    vo.setIsSystem(nullSafe(tag.getIsSystem(), false));

    // 设置分类信息
    if (tag.getCategoryId() != null) {
      vo.setCategoryId(String.valueOf(tag.getCategoryId()));
      TagCategory category = categoryMap != null ? categoryMap.get(tag.getCategoryId()) : null;
      if (category != null) {
        vo.setCategoryName(category.getName());
      }
    }

    // 设置审计信息
    vo.setCreatedBy(tag.getCreatedBy());
    vo.setCreatedDate(tag.getCreatedDate());
    vo.setModifiedBy(tag.getModifiedBy());
    vo.setModifiedDate(tag.getModifiedDate());
    return vo;
  }

  public static TagListVo toListVo(Tag tag, Map<Long, TagCategory> categoryMap) {
    TagListVo vo = new TagListVo();
    vo.setId(tag.getId());
    vo.setName(tag.getName());
    vo.setDescription(tag.getDescription());
    vo.setIsSystem(nullSafe(tag.getIsSystem(), false));

    // 设置分类信息
    if (tag.getCategoryId() != null) {
      vo.setCategoryId(String.valueOf(tag.getCategoryId()));
      TagCategory category = categoryMap != null ? categoryMap.get(tag.getCategoryId()) : null;
      if (category != null) {
        vo.setCategoryName(category.getName());
      }
    }

    // 设置审计信息
    vo.setCreatedBy(tag.getCreatedBy());
    vo.setCreatedDate(tag.getCreatedDate());
    vo.setModifiedBy(tag.getModifiedBy());
    vo.setModifiedDate(tag.getModifiedDate());
    return vo;
  }

  public static GenericSpecification<Tag> getSpecification(TagFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate")
        .orderByFields("id", "createdDate", "modifiedDate", "name")
        .matchSearchFields("name", "description")
        .build();
    return new GenericSpecification<>(filters);
  }
}
