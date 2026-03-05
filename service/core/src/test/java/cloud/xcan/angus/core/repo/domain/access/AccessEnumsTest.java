package cloud.xcan.angus.core.repo.domain.access;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * AccessPrincipalType and AccessPermission enum unit tests.
 */
public class AccessEnumsTest {

    // ---- AccessPrincipalType tests ----

    @Test
    void testPrincipalTypeValues() {
        // Given & When
        AccessPrincipalType[] values = AccessPrincipalType.values();

        // Then: all 5 values exist
        assertThat(values).containsExactlyInAnyOrder(
            AccessPrincipalType.USER,
            AccessPrincipalType.ROLE,
            AccessPrincipalType.GROUP,
            AccessPrincipalType.TOKEN,
            AccessPrincipalType.API_KEY
        );
    }

    @Test
    void testPrincipalTypeValueMapping() {
        // Given & When & Then
        assertThat(AccessPrincipalType.USER.getValue()).isEqualTo("user");
        assertThat(AccessPrincipalType.ROLE.getValue()).isEqualTo("role");
        assertThat(AccessPrincipalType.GROUP.getValue()).isEqualTo("group");
        assertThat(AccessPrincipalType.TOKEN.getValue()).isEqualTo("token");
        assertThat(AccessPrincipalType.API_KEY.getValue()).isEqualTo("api_key");
    }

    @Test
    void testPrincipalTypeCount() {
        // Given & When & Then: exactly 5 values
        assertThat(AccessPrincipalType.values()).hasSize(5);
    }

    // ---- AccessPermission tests ----

    @Test
    void testPermissionValues() {
        // Given & When
        AccessPermission[] values = AccessPermission.values();

        // Then: all 4 values exist
        assertThat(values).containsExactlyInAnyOrder(
            AccessPermission.READ,
            AccessPermission.WRITE,
            AccessPermission.DELETE,
            AccessPermission.ADMIN
        );
    }

    @Test
    void testPermissionValueMapping() {
        // Given & When & Then
        assertThat(AccessPermission.READ.getValue()).isEqualTo("read");
        assertThat(AccessPermission.WRITE.getValue()).isEqualTo("write");
        assertThat(AccessPermission.DELETE.getValue()).isEqualTo("delete");
        assertThat(AccessPermission.ADMIN.getValue()).isEqualTo("admin");
    }

    @Test
    void testPermissionCount() {
        // Given & When & Then: exactly 4 values
        assertThat(AccessPermission.values()).hasSize(4);
    }
}
