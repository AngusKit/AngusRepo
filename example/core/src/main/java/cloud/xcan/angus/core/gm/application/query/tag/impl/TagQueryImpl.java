package cloud.xcan.angus.core.gm.application.query.tag.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tag.TagQuery;
import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagCategory;
import cloud.xcan.angus.core.gm.domain.tag.TagCategoryRepo;
import cloud.xcan.angus.core.gm.domain.tag.TagRepo;
import cloud.xcan.angus.core.gm.domain.tag.TagSearchRepo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.CategoryTagCount;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagStatisticsVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class TagQueryImpl implements TagQuery {

  @Resource
  private TagRepo tagRepo;

  @Resource
  private TagSearchRepo tagSearchRepo;

  @Resource
  private TagCategoryRepo tagCategoryRepo;

  @Override
  public Tag findAndCheck(Long id) {
    return new BizTemplate<Tag>() {
      @Override
      protected Tag process() {
        return tagRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("标签「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Page<Tag> find(GenericSpecification<Tag> spec, PageRequest pageable,
      boolean fullTextSearch, String[] match) {
    return new BizTemplate<Page<Tag>>() {
      @Override
      protected Page<Tag> process() {
        return fullTextSearch
            ? tagSearchRepo.find(spec.getCriteria(), pageable, Tag.class, match)
            : tagRepo.findAll(spec, pageable);
      }
    }.execute();
  }

  @Override
  public List<Tag> findAll() {
    return new BizTemplate<List<Tag>>() {
      @Override
      protected List<Tag> process() {
        return tagRepo.findAll();
      }
    }.execute();
  }

  @Override
  public Tag findByName(String name) {
    return new BizTemplate<Tag>() {
      @Override
      protected Tag process() {
        return tagRepo.findByName(name);
      }
    }.execute();
  }

  @Override
  public List<Tag> findByIds(List<Long> ids) {
    return new BizTemplate<List<Tag>>() {
      @Override
      protected List<Tag> process() {
        return tagRepo.findAllById(ids);
      }
    }.execute();
  }

  @Override
  public TagStatisticsVo getStatistics() {
    return new BizTemplate<TagStatisticsVo>() {
      @Override
      protected TagStatisticsVo process() {
        List<Tag> allTags = tagRepo.findAll();

        TagStatisticsVo vo = new TagStatisticsVo();
        vo.setTotalTags(allTags.size());

        // 统计系统标签和自定义标签
        long systemTagsCount = allTags.stream()
            .filter(t -> Boolean.TRUE.equals(t.getIsSystem()))
            .count();
        vo.setSystemTags((int) systemTagsCount);
        vo.setCustomTags((int) (allTags.size() - systemTagsCount));

        // 统计分类数量
        List<Long> categoryCodes = allTags.stream()
            .map(Tag::getCategoryId)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
        vo.setTotalCategories(categoryCodes.size());

        // 按分类统计标签数量
        Map<Long, Long> categoryCountMap = allTags.stream()
            .filter(t -> t.getCategoryId() != null)
            .collect(Collectors.groupingBy(
                Tag::getCategoryId,
                Collectors.counting()
            ));

        List<CategoryTagCount> categoryStatistics = new ArrayList<>();
        List<TagCategory> allCategories = tagCategoryRepo.findAllById(
            allTags.stream().map(Tag::getCategoryId).collect(Collectors.toSet()));
        Map<Long, TagCategory> categoryMap = allCategories.stream()
            .collect(Collectors.toMap(TagCategory::getId, c -> c));

        for (Long categoryId : categoryCodes) {
          CategoryTagCount count = new CategoryTagCount();
          count.setTagCount(categoryCountMap.getOrDefault(categoryId, 0L).intValue());

          // 查询分类信息
          TagCategory category = categoryMap.get(categoryId);
          if (category != null) {
            count.setCategoryId(category.getId());
            count.setCategoryName(category.getName());
          }
          categoryStatistics.add(count);
        }
        vo.setCategoryStatistics(categoryStatistics);
        return vo;
      }
    }.execute();
  }

  @Override
  public List<Tag> findByCategoryId(Long categoryId) {
    return new BizTemplate<List<Tag>>(false) {
      @Override
      protected List<Tag> process() {
        // 过滤有权限的标签（系统标签或当前租户的标签）
        return tagRepo.findByCategoryId(categoryId);
      }
    }.execute();
  }

}
