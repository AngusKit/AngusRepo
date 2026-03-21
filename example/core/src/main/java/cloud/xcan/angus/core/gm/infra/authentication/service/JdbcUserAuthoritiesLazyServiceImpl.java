package cloud.xcan.angus.core.gm.infra.authentication.service;

import static cloud.xcan.angus.api.commonlink.GMConstant.SYS_ADMIN_ROLE_NAME;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.POLICY_PREFIX;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isNotEmpty;

import cloud.xcan.angus.api.commonlink.role.Role;
import cloud.xcan.angus.core.gm.application.query.role.RoleQuery;
import cloud.xcan.angus.security.model.CustomOAuth2User;
import cloud.xcan.angus.security.repository.JdbcUserAuthoritiesLazyService;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class JdbcUserAuthoritiesLazyServiceImpl implements JdbcUserAuthoritiesLazyService {

  @Resource
  private RoleQuery roleQuery;

  /**
   * 组装授权用户角色和权限信息
   */
  @Override
  public Set<GrantedAuthority> lazyUserAuthorities(CustomOAuth2User user) {
    Set<GrantedAuthority> authorities = new HashSet<>();
    boolean isSysAdmin = user.isSysAdmin();
    if (isSysAdmin) {
      return Set.of(new SimpleGrantedAuthority(SYS_ADMIN_ROLE_NAME));
    }
    List<Role> authRoles = roleQuery.findWideRolesByUserId(PrincipalContext.getUserId());
    initPolicyAuthorities(authorities, authRoles);
    return authorities;
  }

  private void initPolicyAuthorities(Set<GrantedAuthority> authorities, List<Role> authRoles) {
    if (isNotEmpty(authRoles)) {
      for (Role role : authRoles) {
        if (role.getCode().startsWith(POLICY_PREFIX)) {
          authorities.add(new SimpleGrantedAuthority(role.getCode()));
        } else {
          authorities.add(new SimpleGrantedAuthority(POLICY_PREFIX + role.getCode()));
        }
        if (isNotEmpty(role.getPermissions())) {
          role.getPermissions().forEach(permission -> {
            if (isNotEmpty(permission.getActions())) {
              for (String action : permission.getActions()) {
                authorities.add(
                    new SimpleGrantedAuthority(permission.getResource() + ":" + action));
              }
            }
          });
        }
      }
    }
  }

}
