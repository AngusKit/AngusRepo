package cloud.xcan.angus.core.repo.interfaces.format.facade.internal.assembler;

import cloud.xcan.angus.core.repo.domain.format.SetupGuide;
import cloud.xcan.angus.core.repo.domain.format.ValidationResult;
import cloud.xcan.angus.core.repo.domain.format.entity.AptPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.DockerImageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.GoModuleEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.HelmChartEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.MavenMetadataEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.NpmPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.NuGetPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.PyPIPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.RawAssetEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.RpmPackageEntity;
import cloud.xcan.angus.core.repo.domain.repository.RepositoryFormat;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatMetadataVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatSetupGuideVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatSupportedVo;
import cloud.xcan.angus.core.repo.interfaces.format.facade.vo.FormatValidationResultVo;

public class FormatAssembler {

  public static FormatMetadataVo toMavenMetadataVo(MavenMetadataEntity entity) {
    if (entity == null) {
      return null;
    }
    FormatMetadataVo vo = new FormatMetadataVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setFormat(RepositoryFormat.MAVEN);
    vo.setName(entity.getGroupId() + ":" + entity.getArtifactId());
    vo.setVersion(entity.getVersion());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static FormatMetadataVo toDockerMetadataVo(DockerImageEntity entity) {
    if (entity == null) {
      return null;
    }
    FormatMetadataVo vo = new FormatMetadataVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setFormat(RepositoryFormat.DOCKER);
    vo.setName(entity.getImageName());
    vo.setVersion(entity.getTag());
    vo.setDescription(entity.getArchitecture() != null ? entity.getArchitecture() + "/" + entity.getOs() : null);
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static FormatMetadataVo toNpmMetadataVo(NpmPackageEntity entity) {
    if (entity == null) {
      return null;
    }
    FormatMetadataVo vo = new FormatMetadataVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setFormat(RepositoryFormat.NPM);
    vo.setName(entity.getName());
    vo.setDescription(entity.getDescription());
    vo.setMetadata(entity.getKeywords());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static FormatMetadataVo toNuGetMetadataVo(NuGetPackageEntity entity) {
    if (entity == null) {
      return null;
    }
    FormatMetadataVo vo = new FormatMetadataVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setFormat(RepositoryFormat.NUGET);
    vo.setName(entity.getPackageId());
    vo.setVersion(entity.getVersion());
    vo.setDescription(entity.getDescription());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static FormatMetadataVo toPyPIMetadataVo(PyPIPackageEntity entity) {
    if (entity == null) {
      return null;
    }
    FormatMetadataVo vo = new FormatMetadataVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setFormat(RepositoryFormat.PYPI);
    vo.setName(entity.getName());
    vo.setVersion(entity.getVersion());
    vo.setDescription(entity.getSummary());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static FormatMetadataVo toAptMetadataVo(AptPackageEntity entity) {
    if (entity == null) {
      return null;
    }
    FormatMetadataVo vo = new FormatMetadataVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setFormat(RepositoryFormat.APT);
    vo.setName(entity.getPackageName());
    vo.setVersion(entity.getVersion());
    vo.setDescription(entity.getDescription());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static FormatMetadataVo toRpmMetadataVo(RpmPackageEntity entity) {
    if (entity == null) {
      return null;
    }
    FormatMetadataVo vo = new FormatMetadataVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setFormat(RepositoryFormat.YUM);
    vo.setName(entity.getName());
    vo.setVersion(entity.getVersion());
    vo.setDescription(entity.getSummary());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static FormatMetadataVo toRawMetadataVo(RawAssetEntity entity) {
    if (entity == null) {
      return null;
    }
    FormatMetadataVo vo = new FormatMetadataVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setFormat(RepositoryFormat.RAW);
    vo.setName(entity.getFileName());
    vo.setDescription(entity.getContentType());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static FormatMetadataVo toHelmMetadataVo(HelmChartEntity entity) {
    if (entity == null) {
      return null;
    }
    FormatMetadataVo vo = new FormatMetadataVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setFormat(RepositoryFormat.HELM);
    vo.setName(entity.getName());
    vo.setVersion(entity.getVersion());
    vo.setDescription(entity.getDescription());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static FormatMetadataVo toGoMetadataVo(GoModuleEntity entity) {
    if (entity == null) {
      return null;
    }
    FormatMetadataVo vo = new FormatMetadataVo();
    vo.setId(entity.getId());
    vo.setRepositoryId(entity.getRepositoryId());
    vo.setFormat(RepositoryFormat.GO);
    vo.setName(entity.getModulePath());
    vo.setVersion(entity.getVersion());
    vo.setCreatedBy(entity.getCreatedBy());
    vo.setCreatedDate(entity.getCreatedDate());
    vo.setModifiedBy(entity.getModifiedBy());
    vo.setModifiedDate(entity.getModifiedDate());
    return vo;
  }

  public static FormatSetupGuideVo toSetupGuideVo(SetupGuide guide) {
    if (guide == null) {
      return null;
    }
    FormatSetupGuideVo vo = new FormatSetupGuideVo();
    vo.setFormatName(guide.getFormatName());
    vo.setRepositoryUrl(guide.getRepositoryUrl());
    vo.setConfigSnippet(guide.getConfigSnippet());
    vo.setInstructions(guide.getInstructions());
    return vo;
  }

  public static FormatValidationResultVo toValidationResultVo(ValidationResult result) {
    if (result == null) {
      return null;
    }
    FormatValidationResultVo vo = new FormatValidationResultVo();
    vo.setValid(result.isValid());
    vo.setErrors(result.getErrors());
    vo.setWarnings(result.getWarnings());
    return vo;
  }

  public static FormatSupportedVo toSupportedVo(RepositoryFormat format, boolean supported) {
    FormatSupportedVo vo = new FormatSupportedVo();
    vo.setFormat(format);
    vo.setName(format.getValue());
    vo.setSupported(supported);
    return vo;
  }
}
