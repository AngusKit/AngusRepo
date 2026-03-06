package cloud.xcan.angus.core.repo.interfaces.format.facade.internal;

import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toAptMetadataVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toDockerMetadataVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toGoMetadataVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toHelmMetadataVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toMavenMetadataVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toNpmMetadataVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toNuGetMetadataVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toPyPIMetadataVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toRawMetadataVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toRpmMetadataVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toSetupGuideVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toSupportedVo;
import static cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler.FormatAssembler.toValidationResultVo;

import cloud.xcan.angus.core.repo.application.cmd.format.FormatMetadataCmd;
import cloud.xcan.angus.core.repo.application.query.format.FormatMetadataQuery;
import cloud.xcan.angus.core.repo.domain.format.ArtifactFormatHandler;
import cloud.xcan.angus.core.repo.domain.format.FormatHandlerRegistry;
import cloud.xcan.angus.core.repo.domain.format.SetupGuide;
import cloud.xcan.angus.core.repo.domain.format.ValidationResult;
import cloud.xcan.angus.core.repo.domain.repository.RepoEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.application.query.repository.RepositoryQuery;
import cloud.xcan.angus.core.repo.interfaces.format.facade.FormatFacade;
import cloud.xcan.angus.core.repo.interfaces.format.facade.dto.FormatMetadataFindDto;
import cloud.xcan.angus.core.repo.interfaces.format.facade.dto.FormatSetupGuideDto;
import cloud.xcan.angus.core.repo.interfaces.format.facade.dto.FormatValidateDto;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatMetadataVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatSetupGuideVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatSupportedVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatValidationResultVo;
import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class FormatFacadeImpl implements FormatFacade {

  @Resource
  private FormatHandlerRegistry formatHandlerRegistry;

  @Resource
  private FormatMetadataCmd formatMetadataCmd;

  @Resource
  private FormatMetadataQuery formatMetadataQuery;

  @Resource
  private RepositoryQuery repositoryQuery;

  @Override
  public List<FormatSupportedVo> getSupportedFormats() {
    return Arrays.stream(RepositoryFormat.values())
        .map(format -> toSupportedVo(format, formatHandlerRegistry.hasHandler(format)))
        .collect(Collectors.toList());
  }

  @Override
  public FormatSetupGuideVo getSetupGuide(FormatSetupGuideDto dto) {
    RepoEntity repository = repositoryQuery.findAndCheck(dto.getRepositoryId());
    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(repository.getFormat());
    SetupGuide guide = handler.generateSetupGuide(repository, dto.getAuthToken());
    return toSetupGuideVo(guide);
  }

  @Override
  public FormatValidationResultVo validateArtifact(FormatValidateDto dto) {
    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(dto.getFormat());
    ValidationResult result = handler.validateArtifact(null, dto.getFileName());
    return toValidationResultVo(result);
  }

  @Override
  public List<FormatMetadataVo> listMetadata(FormatMetadataFindDto dto) {
    Long repositoryId = dto.getRepositoryId();
    RepositoryFormat format = dto.getFormat();

    switch (format) {
      case MAVEN:
        return formatMetadataQuery.findMavenMetadataByRepositoryId(repositoryId)
            .stream().map(e -> toMavenMetadataVo(e)).collect(Collectors.toList());
      case DOCKER:
        return formatMetadataQuery.findDockerImagesByRepositoryId(repositoryId)
            .stream().map(e -> toDockerMetadataVo(e)).collect(Collectors.toList());
      case NPM:
        return formatMetadataQuery.findNpmPackagesByRepositoryId(repositoryId)
            .stream().map(e -> toNpmMetadataVo(e)).collect(Collectors.toList());
      case NUGET:
        return formatMetadataQuery.findNuGetPackagesByRepositoryId(repositoryId)
            .stream().map(e -> toNuGetMetadataVo(e)).collect(Collectors.toList());
      case PYPI:
        return formatMetadataQuery.findPyPIPackagesByRepositoryId(repositoryId)
            .stream().map(e -> toPyPIMetadataVo(e)).collect(Collectors.toList());
      case APT:
        return formatMetadataQuery.findAptPackagesByRepositoryId(repositoryId)
            .stream().map(e -> toAptMetadataVo(e)).collect(Collectors.toList());
      case YUM:
        return formatMetadataQuery.findRpmPackagesByRepositoryId(repositoryId)
            .stream().map(e -> toRpmMetadataVo(e)).collect(Collectors.toList());
      case RAW:
        return formatMetadataQuery.findRawAssetsByRepositoryId(repositoryId)
            .stream().map(e -> toRawMetadataVo(e)).collect(Collectors.toList());
      case HELM:
        return formatMetadataQuery.findHelmChartsByRepositoryId(repositoryId)
            .stream().map(e -> toHelmMetadataVo(e)).collect(Collectors.toList());
      case GO:
        return formatMetadataQuery.findGoModulesByRepositoryId(repositoryId)
            .stream().map(e -> toGoMetadataVo(e)).collect(Collectors.toList());
      default:
        return Collections.emptyList();
    }
  }

  @Override
  public byte[] getIndex(Long repositoryId) {
    RepoEntity repository = repositoryQuery.findAndCheck(repositoryId);
    ArtifactFormatHandler handler = formatHandlerRegistry.getHandler(repository.getFormat());
    return handler.generateIndex(repository);
  }

  @Override
  public void deleteMetadata(RepositoryFormat format, Long id) {
    switch (format) {
      case MAVEN:
        formatMetadataCmd.deleteMavenMetadata(id);
        break;
      case DOCKER:
        formatMetadataCmd.deleteDockerImage(id);
        break;
      case NPM:
        formatMetadataCmd.deleteNpmPackage(id);
        break;
      case NUGET:
        formatMetadataCmd.deleteNuGetPackage(id);
        break;
      case PYPI:
        formatMetadataCmd.deletePyPIPackage(id);
        break;
      case APT:
        formatMetadataCmd.deleteAptPackage(id);
        break;
      case YUM:
        formatMetadataCmd.deleteRpmPackage(id);
        break;
      case RAW:
        formatMetadataCmd.deleteRawAsset(id);
        break;
      case HELM:
        formatMetadataCmd.deleteHelmChart(id);
        break;
      case GO:
        formatMetadataCmd.deleteGoModule(id);
        break;
      default:
        throw new RuntimeException("Unsupported format: " + format);
    }
  }
}
