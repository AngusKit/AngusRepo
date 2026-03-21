package cloud.xcan.angus.core.gm.infra.persistence.mysql.authentication;

import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUserRepo;
import org.springframework.stereotype.Repository;

@Repository("authenticationUserRepo")
public interface AuthenticationUserRepoPostgres extends AuthenticationUserRepo {

}
