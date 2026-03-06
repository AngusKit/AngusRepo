package cloud.xcan.angus.core.repo.application.cmd.format;

import cloud.xcan.angus.core.repo.domain.format.entity.MavenMetadataEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.DockerImageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.DockerBlobEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.NpmPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.NpmVersionEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.NuGetPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.PyPIPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.PyPIFileEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.AptPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.RpmPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.RawAssetEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.HelmChartEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.GoModuleEntity;

/**
 * Command interface for format-specific metadata write operations.
 * Supports create and delete operations for all 10 repository format metadata types.
 */
public interface FormatMetadataCmd {

  // Maven
  MavenMetadataEntity createMavenMetadata(MavenMetadataEntity entity);
  void deleteMavenMetadata(Long id);

  // Docker
  DockerImageEntity createDockerImage(DockerImageEntity entity);
  void deleteDockerImage(Long id);
  DockerBlobEntity createDockerBlob(DockerBlobEntity entity);
  void deleteDockerBlob(Long id);

  // NPM
  NpmPackageEntity createNpmPackage(NpmPackageEntity entity);
  void deleteNpmPackage(Long id);
  NpmVersionEntity createNpmVersion(NpmVersionEntity entity);
  void deleteNpmVersion(Long id);

  // NuGet
  NuGetPackageEntity createNuGetPackage(NuGetPackageEntity entity);
  void deleteNuGetPackage(Long id);

  // PyPI
  PyPIPackageEntity createPyPIPackage(PyPIPackageEntity entity);
  void deletePyPIPackage(Long id);
  PyPIFileEntity createPyPIFile(PyPIFileEntity entity);
  void deletePyPIFile(Long id);

  // APT
  AptPackageEntity createAptPackage(AptPackageEntity entity);
  void deleteAptPackage(Long id);

  // YUM/RPM
  RpmPackageEntity createRpmPackage(RpmPackageEntity entity);
  void deleteRpmPackage(Long id);

  // Raw
  RawAssetEntity createRawAsset(RawAssetEntity entity);
  void deleteRawAsset(Long id);

  // Helm
  HelmChartEntity createHelmChart(HelmChartEntity entity);
  void deleteHelmChart(Long id);

  // Go
  GoModuleEntity createGoModule(GoModuleEntity entity);
  void deleteGoModule(Long id);
}
