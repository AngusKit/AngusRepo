package cloud.xcan.angus.core.gm.application.query.tag.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.gm.application.query.tag.TagCategoryQuery;
import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagCategory;
import cloud.xcan.angus.core.gm.domain.tag.TagCategoryRepo;
import cloud.xcan.angus.core.gm.domain.tag.TagRepo;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 标签分类查询服务实现
 */
@Service
@Transactional(readOnly = true)
public class TagCategoryQueryImpl implements TagCategoryQuery {

  @Resource
  private TagCategoryRepo tagCategoryRepo;

  @Resource
  private TagRepo tagRepo;

  @Override
  public TagCategory findAndCheck(Long id) {
    return new BizTemplate<TagCategory>() {
      @Override
      protected TagCategory process() {
        return tagCategoryRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("标签分类「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public List<TagCategory> findAll() {
    return new BizTemplate<List<TagCategory>>() {
      @Override
      protected List<TagCategory> process() {
        List<TagCategory> categories = tagCategoryRepo.findAll();
        if (categories.isEmpty()) {
          return categories;
        }

        // 统计每个分类的标签数量
        Map<Long, Long> tagCountMap = tagRepo.findByCategoryIdIn(
                categories.stream().map(TagCategory::getId).collect(Collectors.toSet())).stream()
            .filter(tag -> tag.getCategoryId() != null)
            .collect(Collectors.groupingBy(
                Tag::getCategoryId,
                Collectors.counting()
            ));

        // 设置tagCount
        categories.forEach(category -> {
          Long count = tagCountMap.getOrDefault(category.getId(), 0L);
          category.setTagCount(count.intValue());
        });
        return categories;
      }
    }.execute();
  }

  @Override
  public Integer getTagCount(Long categoryId) {
    return new BizTemplate<Integer>() {
      @Override
      protected Integer process() {
        long count = tagRepo.countByCategoryId(categoryId);
        return (int) count;
      }
    }.execute();
  }

  @Override
  public TagCategory findByCodeAndCheck(String code) {
    return new BizTemplate<TagCategory>() {
      @Override
      protected TagCategory process() {
        return tagCategoryRepo.findByCode(code)
            .orElseThrow(() -> ResourceNotFound.of("标签分类「{0}」不存在", new Object[]{code}));
      }
    }.execute();
  }

}
