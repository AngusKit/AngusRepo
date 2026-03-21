package cloud.xcan.angus.core.gm.application.cmd.tag;

import cloud.xcan.angus.core.gm.domain.tag.Tag;

public interface TagCmd {

  /**
   * 创建标签
   */
  Tag create(Tag tag);

  /**
   * 更新标签
   */
  Tag update(Tag tag);

  /**
   * 删除标签
   */
  void delete(Long id);

}
