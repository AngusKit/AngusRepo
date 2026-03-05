package cloud.xcan.angus.core.repo.domain.upload;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

@NoRepositoryBean
public interface UploadTaskRepo extends BaseRepository<UploadTask, Long> {

  Optional<UploadTask> findByUploadToken(String uploadToken);

  List<UploadTask> findByRepositoryId(Long repositoryId);

  List<UploadTask> findByStatus(UploadStatus status);

  @Query("SELECT COUNT(ut) FROM UploadTask ut WHERE ut.repositoryId = :repositoryId "
      + "AND ut.status = :status")
  long countByRepositoryIdAndStatus(@Param("repositoryId") Long repositoryId,
      @Param("status") UploadStatus status);

  @Modifying
  @Query("UPDATE UploadTask ut SET ut.status = :status WHERE ut.id = :taskId")
  void updateStatus(@Param("taskId") Long taskId, @Param("status") UploadStatus status);

  @Modifying
  @Query("UPDATE UploadTask ut SET ut.uploadedChunks = :uploadedChunks, ut.progress = :progress "
      + "WHERE ut.id = :taskId")
  void updateProgress(@Param("taskId") Long taskId,
      @Param("uploadedChunks") Integer uploadedChunks, @Param("progress") Integer progress);
}
