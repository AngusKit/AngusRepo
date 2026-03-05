package cloud.xcan.angus.core.repo.domain.artifact;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Tests for ArtifactFormat enum including new HELM and GO entries.
 */
public class ArtifactFormatTest {

  @Test
  void testAllFormatValues() {
    ArtifactFormat[] values = ArtifactFormat.values();
    assertThat(values).containsExactlyInAnyOrder(
        ArtifactFormat.MAVEN,
        ArtifactFormat.DOCKER,
        ArtifactFormat.NPM,
        ArtifactFormat.PYPI,
        ArtifactFormat.NUGET,
        ArtifactFormat.APT,
        ArtifactFormat.YUM,
        ArtifactFormat.HELM,
        ArtifactFormat.GO,
        ArtifactFormat.RAW
    );
  }

  @Test
  void testFormatCount() {
    assertThat(ArtifactFormat.values()).hasSize(10);
  }

  @Test
  void testFormatValueMapping() {
    assertThat(ArtifactFormat.MAVEN.getValue()).isEqualTo("maven");
    assertThat(ArtifactFormat.DOCKER.getValue()).isEqualTo("docker");
    assertThat(ArtifactFormat.NPM.getValue()).isEqualTo("npm");
    assertThat(ArtifactFormat.PYPI.getValue()).isEqualTo("pypi");
    assertThat(ArtifactFormat.NUGET.getValue()).isEqualTo("nuget");
    assertThat(ArtifactFormat.APT.getValue()).isEqualTo("apt");
    assertThat(ArtifactFormat.YUM.getValue()).isEqualTo("yum");
    assertThat(ArtifactFormat.HELM.getValue()).isEqualTo("helm");
    assertThat(ArtifactFormat.GO.getValue()).isEqualTo("go");
    assertThat(ArtifactFormat.RAW.getValue()).isEqualTo("raw");
  }
}
