package cloud.xcan.angus.core.gm.interfaces.tag.facade;

import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.BatchQueryTagDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagCreateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagFindDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.dto.TagUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagDetailVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagListVo;
import cloud.xcan.angus.core.gm.interfaces.tag.facade.vo.TagStatisticsVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface TagFacade {

  /**
   * 创建标签
   */
  TagDetailVo create(TagCreateDto dto);

  /**
   * 更新标签
   */
  TagDetailVo update(Long id, TagUpdateDto dto);

  /**
   * 删除标签
   */
  void delete(Long id);

  /**
   * 获取标签详情
   */
  TagDetailVo getDetail(Long id);

  /**
   * 获取标签列表（分页）
   */
  PageResult<TagListVo> list(TagFindDto dto);

  /**
   * 查询标签统计信息
   */
  TagStatisticsVo getStatistics();

  /**
   * 根据名称查询标签
   */
  TagDetailVo getByName(String name);

  /**
   * 批量查询标签
   */
  List<TagDetailVo> batchQuery(BatchQueryTagDto dto);
}
