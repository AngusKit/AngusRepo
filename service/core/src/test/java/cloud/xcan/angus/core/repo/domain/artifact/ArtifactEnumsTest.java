package cloud.xcan.angus.core.repo.domain.artifact;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * ArtifactFormat enum unit tests.
 */
public class ArtifactEnumsTest {

    @Test
    void testValues() {
        // Given & When
        ArtifactFormat[] values = ArtifactFormat.values();

        // Then: all 8 values exist
        assertThat(values).containsExactlyInAnyOrder(
            ArtifactFormat.MAVEN,
            ArtifactFormat.DOCKER,
            ArtifactFormat.NPM,
            ArtifactFormat.PYPI,
            ArtifactFormat.NUGET,
            ArtifactFormat.APT,
            ArtifactFormat.YUM,
            ArtifactFormat.RAW
        );
    }

    @Test
    void testValueMapping() {
        // Given & When & Then
        assertThat(ArtifactFormat.MAVEN.getValue()).isEqualTo("maven");
        assertThat(ArtifactFormat.DOCKER.getValue()).isEqualTo("docker");
        assertThat(ArtifactFormat.NPM.getValue()).isEqualTo("npm");
        assertThat(ArtifactFormat.PYPI.getValue()).isEqualTo("pypi");
        assertThat(ArtifactFormat.NUGET.getValue()).isEqualTo("nuget");
        assertThat(ArtifactFormat.APT.getValue()).isEqualTo("apt");
        assertThat(ArtifactFormat.YUM.getValue()).isEqualTo("yum");
        assertThat(ArtifactFormat.RAW.getValue()).isEqualTo("raw");
    }

    @Test
    void testValueOf() {
        // Given & When & Then
        assertThat(ArtifactFormat.valueOf("MAVEN")).isEqualTo(ArtifactFormat.MAVEN);
        assertThat(ArtifactFormat.valueOf("DOCKER")).isEqualTo(ArtifactFormat.DOCKER);
        assertThat(ArtifactFormat.valueOf("NPM")).isEqualTo(ArtifactFormat.NPM);
        assertThat(ArtifactFormat.valueOf("PYPI")).isEqualTo(ArtifactFormat.PYPI);
        assertThat(ArtifactFormat.valueOf("NUGET")).isEqualTo(ArtifactFormat.NUGET);
        assertThat(ArtifactFormat.valueOf("APT")).isEqualTo(ArtifactFormat.APT);
        assertThat(ArtifactFormat.valueOf("YUM")).isEqualTo(ArtifactFormat.YUM);
        assertThat(ArtifactFormat.valueOf("RAW")).isEqualTo(ArtifactFormat.RAW);
    }

    @Test
    void testCount() {
        // Given & When & Then: exactly 8 values
        assertThat(ArtifactFormat.values()).hasSize(8);
    }
}
