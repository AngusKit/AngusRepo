package cloud.xcan.angus.api.commonlink.oauthuser;

import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository("commonAuthenticationUser")
public interface AuthenticationUserRepo extends BaseRepository<AuthenticationUser, Long> {

  @Query(value = "SELECT * FROM oauth2_user WHERE username = ?1 OR phone = ?1 OR email = ?1", nativeQuery = true)
  List<AuthenticationUser> findByAccount(String account);

  List<AuthenticationUser> findByEmail(String email);

  List<AuthenticationUser> findByPhone(String phone);

  AuthenticationUser findByUsername(String username);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM oauth2_user WHERE id = ?1", nativeQuery = true)
  void deleteById(String id);

  @Transactional
  @Modifying
  @Query(value = "DELETE FROM oauth2_user WHERE tenant_id = ?1", nativeQuery = true)
  void deleteByTenantId(String tenantId);
}
