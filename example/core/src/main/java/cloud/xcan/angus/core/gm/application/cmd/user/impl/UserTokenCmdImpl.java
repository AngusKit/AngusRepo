package cloud.xcan.angus.core.gm.application.cmd.user.impl;

import static cloud.xcan.angus.core.gm.domain.CommonConstant.MAX_USER_TOKEN_QUOTA;
import static cloud.xcan.angus.core.gm.infra.authentication.OAuth2Utils.submitOauth2UserLoginRequest;
import static cloud.xcan.angus.core.gm.infra.utils.TokenUtils.hashToken;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.ACCESS_TOKEN_EXPIRED_DATE;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.CUSTOM_ACCESS_TOKEN;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.CUSTOM_ACCESS_TOKEN_NAME;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.USER_TOKEN_CLIENT_SCOPE;
import static cloud.xcan.angus.spec.principal.PrincipalContext.getClientId;
import static cloud.xcan.angus.spec.principal.PrincipalContext.setRequestAttribute;
import static cloud.xcan.angus.spec.utils.DateUtils.asInstant;
import static java.util.Objects.nonNull;

import cloud.xcan.angus.api.commonlink.application.Application;
import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUser;
import cloud.xcan.angus.api.commonlink.user.enums.TokenStatus;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserTokenCmd;
import cloud.xcan.angus.core.gm.application.query.application.ApplicationQuery;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationClientQuery;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationUserQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserTokenQuery;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.user.UserToken;
import cloud.xcan.angus.core.gm.domain.user.UserTokenRepo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.spring.boot.ApplicationInfo;
import cloud.xcan.angus.remote.message.AbstractResultMessageException;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import cloud.xcan.angus.security.authentication.dao.DaoAuthenticationProvider;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.experimental.BizConstant.AuthKey;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserTokenCmdImpl extends CommCmd<UserToken, Long> implements UserTokenCmd {

  @Resource
  private UserTokenRepo userTokenRepo;

  @Resource
  private UserTokenQuery userTokenQuery;

  @Resource
  private ApplicationQuery appQuery;

  @Resource
  private AuthenticationUserQuery authenticationUserQuery;

  @Resource
  private AuthenticationClientQuery authenticationClientQuery;

  @Resource
  private DaoAuthenticationProvider daoAuthenticationProvider;

  @Resource
  private ApplicationInfo applicationInfo;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserToken create(Long userId, UserToken token, List<String> scopes,
      Integer expiresInDays, String password) {
    return new BizTemplate<UserToken>() {
      Application application;
      AuthenticationUser authenticationUser;
      CustomOAuth2RegisteredClient registeredClient;

      @Override
      protected void checkParams() {
        // 验证用户身份
        authenticationUser = authenticationUserQuery.findAndCheck(userId);
        // 验证用户密码
        authenticationUserQuery.checkPassword(userId, password);

        // 验证客户端身份
        registeredClient = authenticationClientQuery.checkAndFind(getClientId());

        // 验证配额
        long currentCount = userTokenRepo.countByUserId(userId);
        if (currentCount >= MAX_USER_TOKEN_QUOTA) {
          throw ProtocolException.of("令牌配额已满，最多只能创建{0}个令牌",
              new Object[]{MAX_USER_TOKEN_QUOTA});
        }

        // 验证令牌名称唯一性
        UserToken existing = userTokenRepo.findByUserIdAndName(userId, token.getName());
        if (existing != null) {
          throw ProtocolException.of("令牌名称「{0}」已存在", new Object[]{token.getName()});
        }

        // 验证应用是否存在
        application = appQuery.findByCodeAndEditionType(
            token.getAppCode(), applicationInfo.getEditionType()).orElseThrow(
            () -> ResourceNotFound.of("应用「{0}」不存在", new Object[]{token.getAppCode()}));
      }

      @Override
      protected UserToken process() {
        // 缓存用户详情供认证提供者使用
        daoAuthenticationProvider.getUserCache().putUserInCache(authenticationUser.getId(),
            authenticationUser.getUsername(), AuthenticationUser.with(authenticationUser));

        // 为 OAuth2AccessTokenGenerator 设置自定义访问令牌属性
        setRequestAttribute(CUSTOM_ACCESS_TOKEN, true);
        setRequestAttribute(CUSTOM_ACCESS_TOKEN_NAME, token.getName());
        // 如果指定了过期天数，则设置令牌过期日期（null 表示永久有效）
        if (nonNull(expiresInDays)) {
          setRequestAttribute(ACCESS_TOKEN_EXPIRED_DATE,
              asInstant(LocalDateTime.now().plusDays(expiresInDays)));
        }

        // 提交 OAuth2 认证请求以生成令牌
        Map<String, String> result;
        try {
          result = submitOauth2UserLoginRequest(registeredClient.getClientId(),
              registeredClient.getClientSecret(), SignInType.ACCOUNT_PASSWORD,
              authenticationUser.getId(), authenticationUser.getUsername(),
              password, USER_TOKEN_CLIENT_SCOPE);
        } catch (Throwable e) {
          if (e instanceof AbstractResultMessageException) {
            throw (AbstractResultMessageException) e;
          }
          throw new SysException(e.getMessage());
        }

        // 生成令牌
        String plainToken = result.get(AuthKey.ACCESS_TOKEN);

        // 设置令牌信息
        token.setUserId(userId);
        token.setToken(hashToken(plainToken));
        token.setAppId(application.getId());
        token.setAppCode(application.getCode());
        token.setScopes(scopes);
        token.setExpiresAt(LocalDateTime.now().plusDays(expiresInDays));
        token.setStatus(TokenStatus.ACTIVE);
        token.setUsageCount(0);

        // 保存令牌
        UserToken saved = insert(token);

        // 设置原始令牌值（仅用于返回，不保存到数据库）
        saved.setPlainToken(plainToken);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.CREATE,
            ResourceType.USER,
            saved.getId(),
            saved.getName(),
            OperationMessage.USER_TOKEN_CREATE_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserToken update(Long userId, Long tokenId, UserToken token) {
    return new BizTemplate<UserToken>() {
      UserToken existing;

      @Override
      protected void checkParams() {
        // 查找令牌并验证所有权
        existing = userTokenQuery.findAndCheck(userId, tokenId);

        // 验证令牌名称唯一性（排除当前令牌）
        UserToken duplicate = userTokenRepo.findByUserIdAndName(userId, token.getName());
        if (duplicate != null && !duplicate.getId().equals(tokenId)) {
          throw ProtocolException.of("令牌名称「{0}」已存在", new Object[]{token.getName()});
        }
      }

      @Override
      protected UserToken process() {
        // 只更新名称和描述
        existing.setName(token.getName());
        existing.setDescription(token.getDescription());
        UserToken saved = userTokenRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            saved.getId(),
            saved.getName(),
            OperationMessage.USER_TOKEN_UPDATE_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public UserToken revoke(Long userId, Long tokenId) {
    return new BizTemplate<UserToken>() {
      UserToken existing;

      @Override
      protected void checkParams() {
        // 查找令牌并验证所有权
        existing = userTokenQuery.findAndCheck(userId, tokenId);

        // 验证令牌状态
        if (TokenStatus.REVOKED.equals(existing.getStatus())) {
          throw ProtocolException.of("令牌已被撤销");
        }
      }

      @Override
      protected UserToken process() {
        existing.setStatus(TokenStatus.REVOKED);
        existing.setRevokedAt(LocalDateTime.now());
        UserToken saved = userTokenRepo.save(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            saved.getId(),
            saved.getName(),
            OperationMessage.USER_TOKEN_REVOKE_DETAILS,
            new Object[]{saved.getName()}
        );

        return saved;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Long userId, Long tokenId) {
    new BizTemplate<Void>() {
      UserToken existing;

      @Override
      protected void checkParams() {
        // 查找令牌并验证所有权
        existing = userTokenQuery.findAndCheck(userId, tokenId);
      }

      @Override
      protected Void process() {
        String tokenName = existing.getName();
        userTokenRepo.delete(existing);

        // 记录操作日志
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.DELETE,
            ResourceType.USER,
            tokenId,
            tokenName,
            OperationMessage.USER_TOKEN_DELETE_DETAILS,
            new Object[]{tokenName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  protected BaseRepository<UserToken, Long> getRepository() {
    return userTokenRepo;
  }
}
