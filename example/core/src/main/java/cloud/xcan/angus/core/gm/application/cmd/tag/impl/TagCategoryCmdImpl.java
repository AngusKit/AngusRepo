package cloud.xcan.angus.core.gm.application.cmd.tag.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.TagCategoryCmd;
import cloud.xcan.angus.core.gm.application.query.tag.TagCategoryQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.tag.TagCategory;
import cloud.xcan.angus.core.gm.domain.tag.TagCategoryRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 标签分类命令服务实现
 */
@Service
public class TagCategoryCmdImpl extends CommCmd<TagCategory, Long> implements TagCategoryCmd {

  @Resource
  private TagCategoryRepo tagCategoryRepo;

  @Resource
  private TagCategoryQuery tagCategoryQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public TagCategory create(TagCategory category) {
    return new BizTemplate<TagCategory>() {
      @Override
      protected void checkParams() {
        // 检查编码唯一性
        if (tagCategoryRepo.existsByCode(category.getCode())) {
          throw ResourceExisted.of("分类编码「{0}」已存在", new Object[]{category.getCode()});
        }
        // 检查名称唯一性
        if (tagCategoryRepo.existsByName(category.getName())) {
          throw ResourceExisted.of("分类名称「{0}」已存在", new Object[]{category.getName()});
        }
      }

      @Override
      protected TagCategory process() {
        insert(category);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.OTHER,
            category.getId(),
            category.getName(),
            OperationMessage.TAG_CATEGORY_CREATE_DETAILS,
            new Object[]{category.getName()}
        );

        return category;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public TagCategory update(TagCategory category) {
    return new BizTemplate<TagCategory>() {
      TagCategory existing;

      @Override
      protected void checkParams() {
        existing = tagCategoryQuery.findAndCheck(category.getId());
        // 检查是否为系统分类
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
          throw ProtocolException.of("系统分类不允许修改");
        }

        // 检查名称唯一性（排除当前分类）
        if (category.getName() != null && !category.getName().equals(existing.getName())) {
          if (tagCategoryRepo.existsByNameAndIdNot(category.getName(), category.getId())) {
            throw ResourceExisted.of("分类名称「{0}」已存在", new Object[]{category.getName()});
          }
        }
      }

      @Override
      protected TagCategory process() {
        // 更新字段
        if (category.getName() != null) {
          existing.setName(category.getName());
        }
        if (category.getDescription() != null) {
          existing.setDescription(category.getDescription());
        }
        TagCategory saved = tagCategoryRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.OTHER,
            saved.getId(),
            saved.getName(),
            OperationMessage.TAG_CATEGORY_UPDATE_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long id) {
    new BizTemplate<Void>() {
      TagCategory existing;

      @Override
      protected void checkParams() {
        existing = tagCategoryQuery.findAndCheck(id);
        // 检查是否为系统分类
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
          throw ProtocolException.of("系统分类不允许删除");
        }
        // 检查是否还有标签
        Integer tagCount = tagCategoryQuery.getTagCount(id);
        if (tagCount != null && tagCount > 0) {
          throw ProtocolException.of("分类下还有「{0}」个标签，无法删除", new Object[]{tagCount});
        }
      }

      @Override
      protected Void process() {
        String categoryName = existing.getName();
        tagCategoryRepo.deleteById(id);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.OTHER,
            id,
            categoryName,
            OperationMessage.TAG_CATEGORY_DELETE_DETAILS,
            new Object[]{categoryName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<TagCategory, Long> getRepository() {
    return tagCategoryRepo;
  }
}
