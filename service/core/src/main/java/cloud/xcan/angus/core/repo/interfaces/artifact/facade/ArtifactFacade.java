package cloud.xcan.angus.core.repo.interfaces.artifact.facade;

import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactBatchDeleteDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactCreateDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactFindDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.dto.ArtifactUpdateDto;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactDetailVo;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactStatisticsVo;
import cloud.xcan.angus.core.repo.interfaces.artifact.facade.vo.ArtifactVersionVo;
import cloud.xcan.angus.remote.PageResult;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface ArtifactFacade {

  ArtifactDetailVo create(ArtifactCreateDto dto);

  ArtifactDetailVo update(Long id, ArtifactUpdateDto dto);

  void markLatest(Long id);

  void delete(Long id);

  void deleteBatch(ArtifactBatchDeleteDto dto);

  ArtifactDetailVo getById(Long id);

  PageResult<ArtifactDetailVo> list(ArtifactFindDto dto);

  ArtifactStatisticsVo getStatistics();

  void download(Long id, HttpServletResponse response);

  String getDownloadUrl(Long id);

  void addStar(Long artifactId, Long userId);

  void removeStar(Long artifactId, Long userId);

  List<ArtifactVersionVo> getVersions(Long id);
}
