package cloud.xcan.angus.core.repo.application.cmd.format.impl;

import cloud.xcan.angus.core.biz.Biz;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.repo.application.cmd.format.FormatMetadataCmd;
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
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Biz
public class FormatMetadataCmdImpl implements FormatMetadataCmd {

  @Autowired(required = false)
  private MavenMetadataEntityRepo mavenMetadataEntityRepo;

  @Autowired(required = false)
  private DockerImageEntityRepo dockerImageEntityRepo;

  @Autowired(required = false)
  private DockerBlobEntityRepo dockerBlobEntityRepo;

  @Autowired(required = false)
  private NpmPackageEntityRepo npmPackageEntityRepo;

  @Autowired(required = false)
  private NpmVersionEntityRepo npmVersionEntityRepo;

  @Autowired(required = false)
  private NuGetPackageEntityRepo nuGetPackageEntityRepo;

  @Autowired(required = false)
  private PyPIPackageEntityRepo pyPIPackageEntityRepo;

  @Autowired(required = false)
  private PyPIFileEntityRepo pyPIFileEntityRepo;

  @Autowired(required = false)
  private AptPackageEntityRepo aptPackageEntityRepo;

  @Autowired(required = false)
  private RpmPackageEntityRepo rpmPackageEntityRepo;

  @Autowired(required = false)
  private RawAssetEntityRepo rawAssetEntityRepo;

  @Autowired(required = false)
  private HelmChartEntityRepo helmChartEntityRepo;

  @Autowired(required = false)
  private GoModuleEntityRepo goModuleEntityRepo;

  // ===== Maven =====

  @Override
  @Transactional(rollbackFor = Exception.class)
  public MavenMetadataEntity createMavenMetadata(MavenMetadataEntity entity) {
    return new BizTemplate<MavenMetadataEntity>() {
      @Override
      protected void checkParams() {
        // Validate required fields
      }

      @Override
      protected MavenMetadataEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        entity.setModifiedDate(LocalDateTime.now());
        return mavenMetadataEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteMavenMetadata(Long id) {
    mavenMetadataEntityRepo.deleteById(id);
  }

  // ===== Docker =====

  @Override
  @Transactional(rollbackFor = Exception.class)
  public DockerImageEntity createDockerImage(DockerImageEntity entity) {
    return new BizTemplate<DockerImageEntity>() {
      @Override
      protected void checkParams() {
        // Validate required fields
      }

      @Override
      protected DockerImageEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        entity.setModifiedDate(LocalDateTime.now());
        return dockerImageEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteDockerImage(Long id) {
    dockerImageEntityRepo.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public DockerBlobEntity createDockerBlob(DockerBlobEntity entity) {
    return new BizTemplate<DockerBlobEntity>() {
      @Override
      protected DockerBlobEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        return dockerBlobEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteDockerBlob(Long id) {
    dockerBlobEntityRepo.deleteById(id);
  }

  // ===== NPM =====

  @Override
  @Transactional(rollbackFor = Exception.class)
  public NpmPackageEntity createNpmPackage(NpmPackageEntity entity) {
    return new BizTemplate<NpmPackageEntity>() {
      @Override
      protected NpmPackageEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        entity.setModifiedDate(LocalDateTime.now());
        return npmPackageEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteNpmPackage(Long id) {
    npmPackageEntityRepo.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public NpmVersionEntity createNpmVersion(NpmVersionEntity entity) {
    return new BizTemplate<NpmVersionEntity>() {
      @Override
      protected NpmVersionEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        return npmVersionEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteNpmVersion(Long id) {
    npmVersionEntityRepo.deleteById(id);
  }

  // ===== NuGet =====

  @Override
  @Transactional(rollbackFor = Exception.class)
  public NuGetPackageEntity createNuGetPackage(NuGetPackageEntity entity) {
    return new BizTemplate<NuGetPackageEntity>() {
      @Override
      protected NuGetPackageEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        entity.setModifiedDate(LocalDateTime.now());
        return nuGetPackageEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteNuGetPackage(Long id) {
    nuGetPackageEntityRepo.deleteById(id);
  }

  // ===== PyPI =====

  @Override
  @Transactional(rollbackFor = Exception.class)
  public PyPIPackageEntity createPyPIPackage(PyPIPackageEntity entity) {
    return new BizTemplate<PyPIPackageEntity>() {
      @Override
      protected PyPIPackageEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        entity.setModifiedDate(LocalDateTime.now());
        return pyPIPackageEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePyPIPackage(Long id) {
    pyPIPackageEntityRepo.deleteById(id);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public PyPIFileEntity createPyPIFile(PyPIFileEntity entity) {
    return new BizTemplate<PyPIFileEntity>() {
      @Override
      protected PyPIFileEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        return pyPIFileEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deletePyPIFile(Long id) {
    pyPIFileEntityRepo.deleteById(id);
  }

  // ===== APT =====

  @Override
  @Transactional(rollbackFor = Exception.class)
  public AptPackageEntity createAptPackage(AptPackageEntity entity) {
    return new BizTemplate<AptPackageEntity>() {
      @Override
      protected AptPackageEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        entity.setModifiedDate(LocalDateTime.now());
        return aptPackageEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteAptPackage(Long id) {
    aptPackageEntityRepo.deleteById(id);
  }

  // ===== YUM/RPM =====

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RpmPackageEntity createRpmPackage(RpmPackageEntity entity) {
    return new BizTemplate<RpmPackageEntity>() {
      @Override
      protected RpmPackageEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        entity.setModifiedDate(LocalDateTime.now());
        return rpmPackageEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteRpmPackage(Long id) {
    rpmPackageEntityRepo.deleteById(id);
  }

  // ===== Raw =====

  @Override
  @Transactional(rollbackFor = Exception.class)
  public RawAssetEntity createRawAsset(RawAssetEntity entity) {
    return new BizTemplate<RawAssetEntity>() {
      @Override
      protected RawAssetEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        entity.setModifiedDate(LocalDateTime.now());
        return rawAssetEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteRawAsset(Long id) {
    rawAssetEntityRepo.deleteById(id);
  }

  // ===== Helm =====

  @Override
  @Transactional(rollbackFor = Exception.class)
  public HelmChartEntity createHelmChart(HelmChartEntity entity) {
    return new BizTemplate<HelmChartEntity>() {
      @Override
      protected HelmChartEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        entity.setModifiedDate(LocalDateTime.now());
        return helmChartEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteHelmChart(Long id) {
    helmChartEntityRepo.deleteById(id);
  }

  // ===== Go =====

  @Override
  @Transactional(rollbackFor = Exception.class)
  public GoModuleEntity createGoModule(GoModuleEntity entity) {
    return new BizTemplate<GoModuleEntity>() {
      @Override
      protected GoModuleEntity process() {
        entity.setCreatedDate(LocalDateTime.now());
        entity.setModifiedDate(LocalDateTime.now());
        return goModuleEntityRepo.save(entity);
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteGoModule(Long id) {
    goModuleEntityRepo.deleteById(id);
  }
}
