package cloud.xcan.angus.core.repo.application.query.format.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.repo.application.query.format.FormatMetadataQuery;
import cloud.xcan.angus.core.repo.domain.format.entity.AptPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.AptPackageEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.DockerBlobEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.DockerBlobEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.DockerImageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.DockerImageEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.GoModuleEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.GoModuleEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.HelmChartEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.HelmChartEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.MavenMetadataEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.MavenMetadataEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.NpmPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.NpmPackageEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.NpmVersionEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.NpmVersionEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.NuGetPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.NuGetPackageEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.PyPIFileEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.PyPIFileEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.PyPIPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.PyPIPackageEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.RawAssetEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.RawAssetEntityRepo;
import cloud.xcan.angus.core.repo.domain.format.entity.RpmPackageEntity;
import cloud.xcan.angus.core.repo.domain.format.entity.RpmPackageEntityRepo;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Biz
public class FormatMetadataQueryImpl implements FormatMetadataQuery {

  @Resource
  private MavenMetadataEntityRepo mavenMetadataEntityRepo;

  @Resource
  private DockerImageEntityRepo dockerImageEntityRepo;

  @Resource
  private DockerBlobEntityRepo dockerBlobEntityRepo;

  @Resource
  private NpmPackageEntityRepo npmPackageEntityRepo;

  @Resource
  private NpmVersionEntityRepo npmVersionEntityRepo;

  @Resource
  private NuGetPackageEntityRepo nuGetPackageEntityRepo;

  @Resource
  private PyPIPackageEntityRepo pyPIPackageEntityRepo;

  @Resource
  private PyPIFileEntityRepo pyPIFileEntityRepo;

  @Resource
  private AptPackageEntityRepo aptPackageEntityRepo;

  @Resource
  private RpmPackageEntityRepo rpmPackageEntityRepo;

  @Resource
  private RawAssetEntityRepo rawAssetEntityRepo;

  @Resource
  private HelmChartEntityRepo helmChartEntityRepo;

  @Resource
  private GoModuleEntityRepo goModuleEntityRepo;

  // ===== Maven =====

  @Override
  public List<MavenMetadataEntity> findMavenMetadataByRepositoryId(Long repositoryId) {
    return new BizTemplate<List<MavenMetadataEntity>>() {
      @Override
      protected List<MavenMetadataEntity> process() {
        return mavenMetadataEntityRepo.findByRepositoryId(repositoryId);
      }
    }.execute();
  }

  @Override
  public Optional<MavenMetadataEntity> findMavenMetadata(Long repositoryId, String groupId,
      String artifactId, String version) {
    return new BizTemplate<Optional<MavenMetadataEntity>>() {
      @Override
      protected Optional<MavenMetadataEntity> process() {
        return mavenMetadataEntityRepo.findByRepositoryIdAndGroupIdAndArtifactIdAndVersion(
            repositoryId, groupId, artifactId, version);
      }
    }.execute();
  }

  // ===== Docker =====

  @Override
  public List<DockerImageEntity> findDockerImagesByRepositoryId(Long repositoryId) {
    return new BizTemplate<List<DockerImageEntity>>() {
      @Override
      protected List<DockerImageEntity> process() {
        return dockerImageEntityRepo.findByRepositoryId(repositoryId);
      }
    }.execute();
  }

  @Override
  public Optional<DockerImageEntity> findDockerImage(Long repositoryId, String imageName,
      String tag) {
    return new BizTemplate<Optional<DockerImageEntity>>() {
      @Override
      protected Optional<DockerImageEntity> process() {
        return dockerImageEntityRepo.findByRepositoryIdAndImageNameAndTag(
            repositoryId, imageName, tag);
      }
    }.execute();
  }

  @Override
  public Optional<DockerBlobEntity> findDockerBlobByDigest(String digest) {
    return new BizTemplate<Optional<DockerBlobEntity>>() {
      @Override
      protected Optional<DockerBlobEntity> process() {
        return dockerBlobEntityRepo.findByDigest(digest);
      }
    }.execute();
  }

  // ===== NPM =====

  @Override
  public List<NpmPackageEntity> findNpmPackagesByRepositoryId(Long repositoryId) {
    return new BizTemplate<List<NpmPackageEntity>>() {
      @Override
      protected List<NpmPackageEntity> process() {
        return npmPackageEntityRepo.findByRepositoryId(repositoryId);
      }
    }.execute();
  }

  @Override
  public Optional<NpmPackageEntity> findNpmPackage(Long repositoryId, String name) {
    return new BizTemplate<Optional<NpmPackageEntity>>() {
      @Override
      protected Optional<NpmPackageEntity> process() {
        return npmPackageEntityRepo.findByRepositoryIdAndName(repositoryId, name);
      }
    }.execute();
  }

  @Override
  public List<NpmVersionEntity> findNpmVersionsByPackageId(Long packageId) {
    return new BizTemplate<List<NpmVersionEntity>>() {
      @Override
      protected List<NpmVersionEntity> process() {
        return npmVersionEntityRepo.findByPackageId(packageId);
      }
    }.execute();
  }

  // ===== NuGet =====

  @Override
  public List<NuGetPackageEntity> findNuGetPackagesByRepositoryId(Long repositoryId) {
    return new BizTemplate<List<NuGetPackageEntity>>() {
      @Override
      protected List<NuGetPackageEntity> process() {
        return nuGetPackageEntityRepo.findByRepositoryId(repositoryId);
      }
    }.execute();
  }

  @Override
  public Optional<NuGetPackageEntity> findNuGetPackage(Long repositoryId, String packageId,
      String version) {
    return new BizTemplate<Optional<NuGetPackageEntity>>() {
      @Override
      protected Optional<NuGetPackageEntity> process() {
        return nuGetPackageEntityRepo.findByRepositoryIdAndPackageIdAndVersion(
            repositoryId, packageId, version);
      }
    }.execute();
  }

  // ===== PyPI =====

  @Override
  public List<PyPIPackageEntity> findPyPIPackagesByRepositoryId(Long repositoryId) {
    return new BizTemplate<List<PyPIPackageEntity>>() {
      @Override
      protected List<PyPIPackageEntity> process() {
        return pyPIPackageEntityRepo.findByRepositoryId(repositoryId);
      }
    }.execute();
  }

  @Override
  public Optional<PyPIPackageEntity> findPyPIPackage(Long repositoryId, String normalizedName,
      String version) {
    return new BizTemplate<Optional<PyPIPackageEntity>>() {
      @Override
      protected Optional<PyPIPackageEntity> process() {
        return pyPIPackageEntityRepo.findByRepositoryIdAndNormalizedNameAndVersion(
            repositoryId, normalizedName, version);
      }
    }.execute();
  }

  @Override
  public List<PyPIFileEntity> findPyPIFilesByPackageId(Long packageId) {
    return new BizTemplate<List<PyPIFileEntity>>() {
      @Override
      protected List<PyPIFileEntity> process() {
        return pyPIFileEntityRepo.findByPackageId(packageId);
      }
    }.execute();
  }

  // ===== APT =====

  @Override
  public List<AptPackageEntity> findAptPackagesByRepositoryId(Long repositoryId) {
    return new BizTemplate<List<AptPackageEntity>>() {
      @Override
      protected List<AptPackageEntity> process() {
        return aptPackageEntityRepo.findByRepositoryId(repositoryId);
      }
    }.execute();
  }

  // ===== YUM/RPM =====

  @Override
  public List<RpmPackageEntity> findRpmPackagesByRepositoryId(Long repositoryId) {
    return new BizTemplate<List<RpmPackageEntity>>() {
      @Override
      protected List<RpmPackageEntity> process() {
        return rpmPackageEntityRepo.findByRepositoryId(repositoryId);
      }
    }.execute();
  }

  // ===== Raw =====

  @Override
  public List<RawAssetEntity> findRawAssetsByRepositoryId(Long repositoryId) {
    return new BizTemplate<List<RawAssetEntity>>() {
      @Override
      protected List<RawAssetEntity> process() {
        return rawAssetEntityRepo.findByRepositoryId(repositoryId);
      }
    }.execute();
  }

  @Override
  public Optional<RawAssetEntity> findRawAsset(Long repositoryId, String path) {
    return new BizTemplate<Optional<RawAssetEntity>>() {
      @Override
      protected Optional<RawAssetEntity> process() {
        return rawAssetEntityRepo.findByRepositoryIdAndPath(repositoryId, path);
      }
    }.execute();
  }

  // ===== Helm =====

  @Override
  public List<HelmChartEntity> findHelmChartsByRepositoryId(Long repositoryId) {
    return new BizTemplate<List<HelmChartEntity>>() {
      @Override
      protected List<HelmChartEntity> process() {
        return helmChartEntityRepo.findByRepositoryId(repositoryId);
      }
    }.execute();
  }

  @Override
  public Optional<HelmChartEntity> findHelmChart(Long repositoryId, String name, String version) {
    return new BizTemplate<Optional<HelmChartEntity>>() {
      @Override
      protected Optional<HelmChartEntity> process() {
        return helmChartEntityRepo.findByRepositoryIdAndNameAndVersion(
            repositoryId, name, version);
      }
    }.execute();
  }

  // ===== Go =====

  @Override
  public List<GoModuleEntity> findGoModulesByRepositoryId(Long repositoryId) {
    return new BizTemplate<List<GoModuleEntity>>() {
      @Override
      protected List<GoModuleEntity> process() {
        return goModuleEntityRepo.findByRepositoryId(repositoryId);
      }
    }.execute();
  }

  @Override
  public Optional<GoModuleEntity> findGoModule(Long repositoryId, String modulePath,
      String version) {
    return new BizTemplate<Optional<GoModuleEntity>>() {
      @Override
      protected Optional<GoModuleEntity> process() {
        return goModuleEntityRepo.findByRepositoryIdAndModulePathAndVersion(
            repositoryId, modulePath, version);
      }
    }.execute();
  }
}
