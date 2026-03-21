package cloud.xcan.angus.core.gm.domain.tag;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TagCategoryRepo extends BaseRepository<TagCategory, Long> {

  /**
   * 根据租户编码查询所有标签分类
   */
  Optional<TagCategory> findByCode(String code);

  /**
   * 检查租户下编码是否存在
   */
  boolean existsByCode(String code);

  /**
   * 检查租户下名称是否存在
   */
  boolean existsByName(String name);

  /**
   * 检查租户下名称是否存在（排除指定ID）
   */
  boolean existsByNameAndIdNot(String name, Long id);

  /**
   * 检查租户下编码是否存在（排除指定ID）
   */
  @Query(value = "SELECT tc.id FROM gm_tag_category tc WHERE tc.is_system = 1", nativeQuery = true)
  List<Long> findIdsByIsSystem();

  @Query(value = "SELECT tc.id FROM TagCategory tc")
  List<Long> findIdsByTenantId();

}
