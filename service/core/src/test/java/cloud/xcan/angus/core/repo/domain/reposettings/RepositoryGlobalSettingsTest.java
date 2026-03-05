package cloud.xcan.angus.core.repo.domain.reposettings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * RepositoryGlobalSettings entity unit tests.
 */
public class RepositoryGlobalSettingsTest {

    private RepositoryGlobalSettings settings;

    @BeforeEach
    void setUp() {
        settings = new RepositoryGlobalSettings();
    }

    @Test
    void testBasicProperties() {
        // Given
        Long id = 1L;
        String defaultRepository = "maven-central";
        Long storageQuotaGb = 500L;
        Integer retentionDays = 365;
        Long modifiedBy = 1001L;
        LocalDateTime modifiedDate = LocalDateTime.now();

        // When
        settings.setId(id)
                .setDefaultRepository(defaultRepository)
                .setStorageQuotaGb(storageQuotaGb)
                .setRetentionDays(retentionDays)
                .setModifiedBy(modifiedBy)
                .setModifiedDate(modifiedDate);

        // Then
        assertThat(settings.getId()).isEqualTo(id);
        assertThat(settings.getDefaultRepository()).isEqualTo(defaultRepository);
        assertThat(settings.getStorageQuotaGb()).isEqualTo(storageQuotaGb);
        assertThat(settings.getRetentionDays()).isEqualTo(retentionDays);
        assertThat(settings.getModifiedBy()).isEqualTo(modifiedBy);
        assertThat(settings.getModifiedDate()).isEqualTo(modifiedDate);
    }

    @Test
    void testDefaultValues() {
        // Given: a newly created settings entity
        RepositoryGlobalSettings newSettings = new RepositoryGlobalSettings();

        // Then: verify default values
        assertThat(newSettings.getAnonymousAccess()).isFalse();
        assertThat(newSettings.getIndexingEnabled()).isTrue();
        assertThat(newSettings.getCompressionEnabled()).isTrue();
        assertThat(newSettings.getAutoCleanup()).isFalse();
        assertThat(newSettings.getDeduplicationEnabled()).isFalse();
    }

    @Test
    void testChainedSetters() {
        // Given
        Long id = 1L;
        String defaultRepository = "npm-public";
        Boolean anonymousAccess = true;

        // When
        RepositoryGlobalSettings result = settings.setId(id)
                                                  .setDefaultRepository(defaultRepository)
                                                  .setAnonymousAccess(anonymousAccess);

        // Then: chained calls return the same instance
        assertThat(result).isSameAs(settings);
        assertThat(settings.getId()).isEqualTo(id);
        assertThat(settings.getDefaultRepository()).isEqualTo(defaultRepository);
        assertThat(settings.getAnonymousAccess()).isTrue();
    }

    @Test
    void testIdentityMethod() {
        // Given
        Long id = 42L;
        settings.setId(id);

        // When & Then
        assertThat(settings.identity()).isEqualTo(id);
    }

    @Test
    void testIdentityMethodWithNullId() {
        // Given
        settings.setId(null);

        // When & Then
        assertThat(settings.identity()).isNull();
    }
}
