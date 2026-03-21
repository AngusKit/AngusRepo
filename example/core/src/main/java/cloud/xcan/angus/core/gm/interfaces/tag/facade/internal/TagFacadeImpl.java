package cloud.xcan.angus.core.gm.interfaces.tag.facade.internal;

import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.tag.TagCmd;
import cloud.xcan.angus.core.gm.application.query.tag.TagCategoryQuery;
import cloud.xcan.angus.core.gm.application.query.tag.TagQuery;
import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagCategory;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.TagFacade;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.BatchQueryTagDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.internal.assembler.TagAssembler;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class TagFacadeImpl implements TagFacade {

  @Resource
  private TagCmd tagCmd;

  @Resource
  private TagQuery tagQuery;

  @Resource
  private TagCategoryQuery tagCategoryQuery;

  @NameJoin
  @Override
  public TagDetailVo create(TagCreateDto dto) {
    Tag tag = TagAssembler.toCreateDomain(dto);
    Tag saved = tagCmd.create(tag);
    Map<Long, TagCategory> categoryMap = buildCategoryMap(List.of(saved));
    return TagAssembler.toDetailVo(saved, categoryMap);
  }

  @NameJoin
  @Override
  public TagDetailVo update(Long id, TagUpdateDto dto) {
    Tag tag = TagAssembler.toUpdateDomain(id, dto);
    Tag saved = tagCmd.update(tag);
    Map<Long, TagCategory> categoryMap = buildCategoryMap(List.of(saved));
    return TagAssembler.toDetailVo(saved, categoryMap);
  }

  @Override
  public void delete(Long id) {
    tagCmd.delete(id);
  }

  @NameJoin
  @Override
  public TagDetailVo getDetail(Long id) {
    Tag tag = tagQuery.findAndCheck(id);
    Map<Long, TagCategory> categoryMap = buildCategoryMap(List.of(tag));
    return TagAssembler.toDetailVo(tag, categoryMap);
  }

  @NameJoin
  @Override
  public PageResult<TagListVo> list(TagFindDto dto) {
    GenericSpecification<Tag> spec = TagAssembler.getSpecification(dto);
    Page<Tag> page = tagQuery.find(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    Map<Long, TagCategory> categoryMap = buildCategoryMap(page.getContent());
    return buildVoPageResult(page, tag -> TagAssembler.toListVo(tag, categoryMap));
  }

  @Override
  public TagStatisticsVo getStatistics() {
    return tagQuery.getStatistics();
  }

  @NameJoin
  @Override
  public TagDetailVo getByName(String name) {
    Tag tag = tagQuery.findByName(name);
    if (tag == null) {
      return null;
    }
    Map<Long, TagCategory> categoryMap = buildCategoryMap(List.of(tag));
    return TagAssembler.toDetailVo(tag, categoryMap);
  }

  @NameJoin
  @Override
  public List<TagDetailVo> batchQuery(BatchQueryTagDto dto) {
    List<Tag> tags = tagQuery.findByIds(dto.getIds());
    // 批量加载分类信息以提高性能
    Map<Long, TagCategory> categoryMap = buildCategoryMap(tags);
    return tags.stream()
        .map(tag -> TagAssembler.toDetailVo(tag, categoryMap))
        .collect(Collectors.toList());
  }

  private Map<Long, TagCategory> buildCategoryMap(List<Tag> tags) {
    List<TagCategory> categories = tagCategoryQuery.findAll();
    // 提取所有标签使用的分类code
    List<Long> categoryCodes = tags.stream()
        .map(Tag::getCategoryId)
        .filter(Objects::nonNull)
        .distinct()
        .collect(Collectors.toList());
    // 构建映射
    return categories.stream()
        .filter(c -> categoryCodes.contains(c.getId()))
        .collect(Collectors.toMap(TagCategory::getId, c -> c));
  }
}
