package cloud.xcan.angus.core.gm.domain.tag;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TagRepo extends BaseRepository<Tag, Long> {

  Tag findByName(String name);

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);

  List<Tag> findByCategoryId(Long categoryId);

  List<Tag> findByCategoryIdIn(Collection<Long> categoryIds);

  @Query("SELECT COUNT(t) FROM Tag t WHERE t.categoryId = ?1")
  long countByCategoryId(Long categoryId);

}
