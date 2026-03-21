package cloud.xcan.angus.core.gm.application.cmd.tag.impl;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.tag.TagCmd;
import cloud.xcan.angus.core.gm.application.query.tag.TagQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.tag.Tag;
import cloud.xcan.angus.core.gm.domain.tag.TagRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.CoreUtils;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceExisted;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagCmdImpl extends CommCmd<Tag, Long> implements TagCmd {

  @Resource
  private TagRepo tagRepo;

  @Resource
  private TagQuery tagQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Tag create(Tag tag) {
    return new BizTemplate<Tag>() {
      @Override
      protected void checkParams() {
        // 检查同一租户下名称是否已存在
        if (tagRepo.existsByName(tag.getName())) {
          throw ResourceExisted.of("标签名称「{0}」已存在", new Object[]{tag.getName()});
        }
      }

      @Override
      protected Tag process() {
        insert(tag);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.OTHER,
            tag.getId(),
            tag.getName(),
            OperationMessage.TAG_CREATE_DETAILS,
            new Object[]{tag.getName()}
        );
        return tag;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Tag update(Tag tag) {
    return new BizTemplate<Tag>() {
      Tag existing;

      @Override
      protected void checkParams() {
        existing = tagQuery.findAndCheck(tag.getId());

        // 检查是否为系统标签
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
          throw ProtocolException.of("系统标签不允许修改");
        }

        if (tagRepo.existsByNameAndIdNot(tag.getName(), tag.getId())) {
          throw ResourceExisted.of("标签名称「{0}」已存在", new Object[]{tag.getName()});
        }
      }

      @Override
      protected Tag process() {
        CoreUtils.copyPropertiesIgnoreNull(tag, existing);
        Tag saved = tagRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.OTHER,
            saved.getId(),
            saved.getName(),
            OperationMessage.TAG_UPDATE_DETAILS,
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
      Tag existing;

      @Override
      protected void checkParams() {
        existing = tagQuery.findAndCheck(id);
        // 检查是否为系统标签
        if (Boolean.TRUE.equals(existing.getIsSystem())) {
          throw ProtocolException.of("系统标签不允许删除");
        }
      }

      @Override
      protected Void process() {
        String tagName = existing.getName();
        tagRepo.deleteById(id);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.OTHER,
            id,
            tagName,
            OperationMessage.TAG_DELETE_DETAILS,
            new Object[]{tagName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<Tag, Long> getRepository() {
    return tagRepo;
  }
}
