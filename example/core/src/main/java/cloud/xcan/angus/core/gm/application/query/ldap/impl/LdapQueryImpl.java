package cloud.xcan.angus.core.gm.application.query.ldap.impl;

import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isUserAction;

import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.PermissionCheck;
import cloud.xcan.angus.core.gm.application.query.ldap.LdapQuery;
import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapRepo;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapStatus;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapType;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LdapQueryImpl implements LdapQuery {

  @Resource
  private LdapRepo ldapRepo;

  @Override
  public Optional<Ldap> findById(Long id) {
    return new BizTemplate<Optional<Ldap>>(false) {
      @Override
      protected void checkParams() {
        if (isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }
      }

      @Override
      protected Optional<Ldap> process() {
        return ldapRepo.findById(id);
      }
    }.execute();
  }

  @Override
  public Ldap findAndCheck(Long id) {
    return new BizTemplate<Ldap>(false) {
      @Override
      protected void checkParams() {
        if (isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }
      }

      @Override
      protected Ldap process() {
        return ldapRepo.findById(id)
            .orElseThrow(() -> ResourceNotFound.of("LDAP配置「{0}」不存在", new Object[]{id}));
      }
    }.execute();
  }

  @Override
  public Optional<Ldap> findByName(String name) {
    return new BizTemplate<Optional<Ldap>>(false) {
      @Override
      protected void checkParams() {
        if (isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }
      }

      @Override
      protected Optional<Ldap> process() {
        return ldapRepo.findByName(name);
      }
    }.execute();
  }

  @Override
  public List<Ldap> findByType(LdapType type) {
    return new BizTemplate<List<Ldap>>(false) {
      @Override
      protected void checkParams() {
        if (isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }
      }

      @Override
      protected List<Ldap> process() {
        return ldapRepo.findByType(type);
      }
    }.execute();
  }

  @Override
  public List<Ldap> findByStatus(LdapStatus status) {
    return new BizTemplate<List<Ldap>>(false) {
      @Override
      protected void checkParams() {
        if (isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }
      }

      @Override
      protected List<Ldap> process() {
        return ldapRepo.findByStatus(status);
      }
    }.execute();
  }

  @Override
  public List<Ldap> findAll() {
    return new BizTemplate<List<Ldap>>(false) {
      @Override
      protected void checkParams() {
        if (isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }
      }

      @Override
      protected List<Ldap> process() {
        return ldapRepo.findAll();
      }
    }.execute();
  }

  @Override
  public List<Ldap> findByEnabled(Boolean enabled) {
    return new BizTemplate<List<Ldap>>(false) {
      @Override
      protected void checkParams() {
        if (isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }
      }

      @Override
      protected List<Ldap> process() {
        return ldapRepo.findByEnabled(enabled);
      }
    }.execute();
  }

  @Override
  public Ldap getCurrentConfig() {
    return new BizTemplate<Ldap>(false) {
      @Override
      protected void checkParams() {
        if (isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }
      }

      @Override
      protected Ldap process() {
        // 如果同一租户配置了多个LDAP，返回第一个启用的配置
        List<Ldap> enabledConfigs = ldapRepo.findByEnabled(true);
        return enabledConfigs.stream().findFirst().orElse(null);
      }
    }.execute();
  }

  @Override
  public Map<String, String> getFieldMapping(Long id) {
    return new BizTemplate<Map<String, String>>(false) {
      @Override
      protected void checkParams() {
        if (isUserAction()) {
          PermissionCheck.checkCloudTenantSecurity();
        }
      }

      @Override
      protected Map<String, String> process() {
        Ldap config = findAndCheck(id);
        if (config.getFieldMapping() != null && !config.getFieldMapping().isEmpty()) {
          return config.getFieldMapping();
        }
        return getDefaultFieldMapping();
      }
    }.execute();
  }

  private static Map<String, String> getDefaultFieldMapping() {
    return Map.of(
        "uid", "uid",
        "cn", "cn",
        "mail", "mail",
        "department", "department",
        "title", "title",
        "mobile", "mobile"
    );
  }
}
