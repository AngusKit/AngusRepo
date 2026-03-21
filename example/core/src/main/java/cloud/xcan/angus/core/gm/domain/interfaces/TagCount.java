package cloud.xcan.angus.core.gm.domain.interfaces;

/**
 * Tag统计投影接口 用于GROUP BY查询结果映射
 */
public interface TagCount {

  /**
   * 获取标签名称
   */
  String getTag();

  /**
   * 获取该标签的接口数量
   */
  Long getCount();
}
