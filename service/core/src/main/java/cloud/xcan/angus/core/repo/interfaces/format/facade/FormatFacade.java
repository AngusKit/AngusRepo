package cloud.xcan.angus.core.repo.interfaces.format.facade;

import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.interfaces.format.facade.dto.FormatMetadataFindDto;
import cloud.xcan.angus.core.repo.interfaces.format.facade.dto.FormatSetupGuideDto;
import cloud.xcan.angus.core.repo.interfaces.format.facade.dto.FormatValidateDto;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatMetadataVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatSetupGuideVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatSupportedVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatValidationResultVo;
import java.util.List;

/**
 * Facade interface for format service operations.
 */
public interface FormatFacade {

  List<FormatSupportedVo> getSupportedFormats();

  FormatSetupGuideVo getSetupGuide(FormatSetupGuideDto dto);

  FormatValidationResultVo validateArtifact(FormatValidateDto dto);

  List<FormatMetadataVo> listMetadata(FormatMetadataFindDto dto);

  byte[] getIndex(Long repositoryId);

  void deleteMetadata(RepositoryFormat format, Long id);
}
