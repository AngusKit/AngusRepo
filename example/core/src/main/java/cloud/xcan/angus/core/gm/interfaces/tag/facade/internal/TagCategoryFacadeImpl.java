package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tag.TagCategoryCmd;
import cloud.xcan.angus.core.gm.application.query.tag.TagCategoryQuery;
import cloud.xcan.angus.core.gm.application.query.tag.TagQuery;
import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagCategory;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.TagCategoryFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.CreateTagCategoryDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.UpdateTagCategoryDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.TagAssembler;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.TagCategoryAssembler;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagCategoryVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 标签分类门面实现
 */
@Component
public class TagCategoryFacadeImpl implements TagCategoryFacade {

  @Resource
  private TagCategoryCmd tagCategoryCmd;

  @Resource
  private TagCategoryQuery tagCategoryQuery;

  @Resource
  private TagQuery tagQuery;

  @NameJoin
  @Override
  public TagCategoryVo create(CreateTagCategoryDto dto) {
    TagCategory category = TagCategoryAssembler.toCreateDomain(dto);
    TagCategory saved = tagCategoryCmd.create(category);
    return TagCategoryAssembler.toVo(saved);
  }

  @NameJoin
  @Override
  public TagCategoryVo update(Long id, UpdateTagCategoryDto dto) {
    TagCategory category = TagCategoryAssembler.toUpdateDomain(id, dto);
    TagCategory saved = tagCategoryCmd.update(category);
    return TagCategoryAssembler.toVo(saved);
  }

  @Override
  public void delete(Long id) {
    tagCategoryCmd.delete(id);
  }

  @NameJoin
  @Override
  public TagCategoryVo getById(Long id) {
    TagCategory category = tagCategoryQuery.findAndCheck(id);
    Integer tagCount = tagCategoryQuery.getTagCount(id);
    category.setTagCount(tagCount);
    return TagCategoryAssembler.toVo(category);
  }

  @NameJoin
  @Override
  public List<TagCategoryVo> list() {
    List<TagCategory> categories = tagCategoryQuery.findAll();
    return TagCategoryAssembler.toVoList(categories);
  }

  @NameJoin
  @Override
  public List<TagListVo> getTagListByCategoryCode(String code) {
    // 根据编码查询分类
    TagCategory category = tagCategoryQuery.findByCodeAndCheck(code);
    // 查询该分类下的所有标签
    List<Tag> tags = tagQuery.findByCategoryId(category.getId());
    // 构建分类映射
    Map<Long, TagCategory> categoryMap = Collections.singletonMap(category.getId(), category);
    // 转换为VO列表
    return tags.stream()
        .map(tag -> TagAssembler.toListVo(tag, categoryMap))
        .collect(Collectors.toList());
  }
}
