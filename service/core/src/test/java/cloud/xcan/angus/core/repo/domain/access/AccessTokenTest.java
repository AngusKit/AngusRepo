package cloud.xcan.angus.core.repo.domain.access;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

/**
 * AccessToken entity unit tests.
 */
public class AccessTokenTest {

    private AccessToken token;

    @BeforeEach
    void setUp() {
        token = new AccessToken();
    }

    @Test
    void testBasicProperties() {
        // Given
        Long id = 1L;
        Long repositoryId = 100L;
        String name = "CI Token";
        String description = "Token for CI/CD pipeline";
        String tokenHash = "hash-abc123";
        String permissions = "[\"read\",\"write\"]";
        String ipWhitelist = "[\"10.0.0.0/8\"]";
        LocalDateTime lastUsed = LocalDateTime.now();

        // When
        token.setId(id)
             .setRepositoryId(repositoryId)
             .setName(name)
             .setDescription(description)
             .setTokenHash(tokenHash)
             .setPermissions(permissions)
             .setIpWhitelist(ipWhitelist)
             .setLastUsed(lastUsed);

        // Then
        assertThat(token.getId()).isEqualTo(id);
        assertThat(token.getRepositoryId()).isEqualTo(repositoryId);
        assertThat(token.getName()).isEqualTo(name);
        assertThat(token.getDescription()).isEqualTo(description);
        assertThat(token.getTokenHash()).isEqualTo(tokenHash);
        assertThat(token.getPermissions()).isEqualTo(permissions);
        assertThat(token.getIpWhitelist()).isEqualTo(ipWhitelist);
        assertThat(token.getLastUsed()).isEqualTo(lastUsed);
    }

    @Test
    void testDefaultValues() {
        // Given: a newly created access token entity
        AccessToken newToken = new AccessToken();

        // Then: verify default values
        assertThat(newToken.getEnabled()).isTrue();
        assertThat(newToken.getUsageCount()).isEqualTo(0L);
    }

    @Test
    void testIsExpiredWhenExpired() {
        // Given
        token.setExpiresAt(LocalDateTime.now().minusHours(1));

        // When & Then
        assertThat(token.isExpired()).isTrue();
    }

    @Test
    void testIsExpiredWhenNotExpired() {
        // Given
        token.setExpiresAt(LocalDateTime.now().plusHours(1));

        // When & Then
        assertThat(token.isExpired()).isFalse();
    }

    @Test
    void testIsExpiredWhenNull() {
        // Given
        token.setExpiresAt(null);

        // When & Then
        assertThat(token.isExpired()).isFalse();
    }

    @Test
    void testChainedSetters() {
        // Given
        Long id = 1L;
        String name = "Deploy Token";
        String tokenHash = "hash-xyz";

        // When
        AccessToken result = token.setId(id)
                                  .setName(name)
                                  .setTokenHash(tokenHash)
                                  .setEnabled(false);

        // Then: chained calls return the same instance
        assertThat(result).isSameAs(token);
        assertThat(token.getId()).isEqualTo(id);
        assertThat(token.getName()).isEqualTo(name);
        assertThat(token.getTokenHash()).isEqualTo(tokenHash);
        assertThat(token.getEnabled()).isFalse();
    }

    @Test
    void testIdentityMethod() {
        // Given
        Long id = 42L;
        token.setId(id);

        // When & Then
        assertThat(token.identity()).isEqualTo(id);
    }

    @Test
    void testIdentityMethodWithNullId() {
        // Given
        token.setId(null);

        // When & Then
        assertThat(token.identity()).isNull();
    }
}
