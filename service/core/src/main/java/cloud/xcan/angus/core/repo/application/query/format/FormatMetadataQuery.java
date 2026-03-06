package cloud.xcan.angus.core.repo.application.query.format;

import cloud.xcan.angus.core.repo.domain.format.entity.AptPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.DockerBlobEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.DockerImageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.GoModuleEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.HelmChartEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.MavenMetadataEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.NpmPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.NpmVersionEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.NuGetPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.PyPIFileEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.PyPIPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.RawAssetEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.RpmPackageEntity;
import java.util.List;
import java.util.Optional;

/**
 * Query interface for format-specific metadata read operations.
 */
public interface FormatMetadataQuery {

  // Maven
  List<MavenMetadataEntity> findMavenMetadataByRepositoryId(Long repositoryId);
  Optional<MavenMetadataEntity> findMavenMetadata(Long repositoryId, String groupId, String artifactId, String version);

  // Docker
  List<DockerImageEntity> findDockerImagesByRepositoryId(Long repositoryId);
  Optional<DockerImageEntity> findDockerImage(Long repositoryId, String imageName, String tag);
  Optional<DockerBlobEntity> findDockerBlobByDigest(String digest);

  // NPM
  List<NpmPackageEntity> findNpmPackagesByRepositoryId(Long repositoryId);
  Optional<NpmPackageEntity> findNpmPackage(Long repositoryId, String name);
  List<NpmVersionEntity> findNpmVersionsByPackageId(Long packageId);

  // NuGet
  List<NuGetPackageEntity> findNuGetPackagesByRepositoryId(Long repositoryId);
  Optional<NuGetPackageEntity> findNuGetPackage(Long repositoryId, String packageId, String version);

  // PyPI
  List<PyPIPackageEntity> findPyPIPackagesByRepositoryId(Long repositoryId);
  Optional<PyPIPackageEntity> findPyPIPackage(Long repositoryId, String normalizedName, String version);
  List<PyPIFileEntity> findPyPIFilesByPackageId(Long packageId);

  // APT
  List<AptPackageEntity> findAptPackagesByRepositoryId(Long repositoryId);

  // YUM/RPM
  List<RpmPackageEntity> findRpmPackagesByRepositoryId(Long repositoryId);

  // Raw
  List<RawAssetEntity> findRawAssetsByRepositoryId(Long repositoryId);
  Optional<RawAssetEntity> findRawAsset(Long repositoryId, String path);

  // Helm
  List<HelmChartEntity> findHelmChartsByRepositoryId(Long repositoryId);
  Optional<HelmChartEntity> findHelmChart(Long repositoryId, String name, String version);

  // Go
  List<GoModuleEntity> findGoModulesByRepositoryId(Long repositoryId);
  Optional<GoModuleEntity> findGoModule(Long repositoryId, String modulePath, String version);
}
