package cloud.xcan.angus.core.gm.infra.persistence.postgres.authentication;

import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUserRepo;
import org.springframework.stereotype.Repository;

@Repository("authenticationUserRepo")
public interface AuthenticationUserRepoMysql extends AuthenticationUserRepo {

}
