package cloud.xcan.angus.core.repo.domain.upload;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UploadChunkRepo extends BaseRepository<UploadChunk, Long> {

  List<UploadChunk> findByUploadTaskId(Long uploadTaskId);

  long countByUploadTaskId(Long uploadTaskId);

  void deleteByUploadTaskId(Long uploadTaskId);
}
