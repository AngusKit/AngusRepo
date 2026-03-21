package cloud.xcan.angus.core.gm.application.cmd.authentication.impl;

import static cloud.xcan.angus.api.commonlink.GMConstant.CACHE_EMAIL_CHECK_SECRET_PREFIX;
import static cloud.xcan.angus.api.commonlink.GMConstant.CACHE_PASSWORD_ERROR_LOCKED_PREFIX;
import static cloud.xcan.angus.api.commonlink.GMConstant.CACHE_PASSWORD_ERROR_NUM_PREFIX;
import static cloud.xcan.angus.api.commonlink.GMConstant.CACHE_SMS_CHECK_SECRET_PREFIX;
import static cloud.xcan.angus.api.commonlink.GMConstant.REGISTER_VERIFICATION_TEMPLATE_CODE;
import static cloud.xcan.angus.api.commonlink.GMConstant.TEMPLATE_CODE_LOGIN_VERIFICATION;
import static cloud.xcan.angus.api.commonlink.GMConstant.TEMPLATE_CODE_RETRIEVE_PASSWORD_VERIFICATION;
import static cloud.xcan.angus.api.commonlink.GMConstant.TEMPLATE_CODE_VERIFICATION_CODE;
import static cloud.xcan.angus.api.commonlink.GMConstant.VERIFICATION_CODE_NAME;
import static cloud.xcan.angus.api.commonlink.GMConstant.VERIFICATION_CODE_VALID_MINUTE;
import static cloud.xcan.angus.api.commonlink.client.enums.ClientSource.isUserSignIn;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertNotEmpty;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertResourceNotFound;
import static cloud.xcan.angus.core.biz.ProtocolAssert.assertTrue;
import static cloud.xcan.angus.core.gm.application.converter.AuthorizationConverter.toLoginDevice;
import static cloud.xcan.angus.core.gm.application.converter.AuthorizationConverter.toLoginHistory;
import static cloud.xcan.angus.core.gm.application.converter.EmailConverter.getEmailVerificationCodeCacheKey;
import static cloud.xcan.angus.core.gm.application.converter.EmailConverter.getEmailVerificationCodeRepeatCheckKey;
import static cloud.xcan.angus.core.gm.application.converter.SmsConverter.getSmsVerificationCodeCacheKey;
import static cloud.xcan.angus.core.gm.application.converter.SmsConverter.getSmsVerificationCodeRepeatCheckKey;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.toSignUpTenant;
import static cloud.xcan.angus.core.gm.application.converter.UserConverter.toSignUpUser;
import static cloud.xcan.angus.core.gm.domain.TipMessage.EMAIL_VERIFY_CODE_EMPTY;
import static cloud.xcan.angus.core.gm.domain.TipMessage.EMAIL_VERIFY_CODE_ERROR;
import static cloud.xcan.angus.core.gm.domain.TipMessage.EMAIL_VERIFY_CODE_EXPIRED;
import static cloud.xcan.angus.core.gm.domain.TipMessage.LOGIN_ACCOUNT_EMPTY;
import static cloud.xcan.angus.core.gm.domain.TipMessage.LOGIN_DEVICE_ID_EMPTY;
import static cloud.xcan.angus.core.gm.domain.TipMessage.LOGIN_PASSWORD_ERROR_LOCKED_RETRY_CODE;
import static cloud.xcan.angus.core.gm.domain.TipMessage.LOGIN_PASSWORD_ERROR_LOCKED_RETRY_T;
import static cloud.xcan.angus.core.gm.domain.TipMessage.LOGIN_PASSWORD_ERROR_OVER_LIMIT_CODE;
import static cloud.xcan.angus.core.gm.domain.TipMessage.LOGIN_PASSWORD_ERROR_OVER_LIMIT_T;
import static cloud.xcan.angus.core.gm.domain.TipMessage.SMS_VERIFY_CODE_EMPTY;
import static cloud.xcan.angus.core.gm.domain.TipMessage.SMS_VERIFY_CODE_ERROR;
import static cloud.xcan.angus.core.gm.domain.TipMessage.SMS_VERIFY_CODE_EXPIRED;
import static cloud.xcan.angus.core.gm.infra.authentication.OAuth2Utils.submitOauth2RenewRequest;
import static cloud.xcan.angus.core.gm.infra.authentication.OAuth2Utils.submitOauth2UserLoginRequest;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.getApplicationInfo;
import static cloud.xcan.angus.core.utils.PrincipalContextUtils.isCloudServiceEdition;
import static cloud.xcan.angus.remote.message.ProtocolException.M.EMAIL_NOT_EXIST_T;
import static cloud.xcan.angus.remote.message.ProtocolException.M.MOBILE_NOT_EXIST_T;
import static cloud.xcan.angus.remote.message.ProtocolException.M.PARAM_MISSING_KEY;
import static cloud.xcan.angus.remote.message.ProtocolException.M.QUERY_FIELD_EMPTY_T;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.ACCESS_TOKEN;
import static cloud.xcan.angus.spec.experimental.BizConstant.AuthKey.USER_TOKEN_CLIENT_SCOPE;
import static cloud.xcan.angus.spec.experimental.BizConstant.MAX_LINK_SECRET_LENGTH;
import static cloud.xcan.angus.spec.experimental.BizConstant.XCAN_TENANT_PLATFORM_CODE;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isBlank;
import static cloud.xcan.angus.spec.utils.ObjectUtils.isEmpty;
import static java.lang.Integer.parseInt;
import static java.lang.Long.valueOf;
import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import cloud.xcan.angus.api.commonlink.GMConstant;
import cloud.xcan.angus.api.enums.EditionType;
import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.commonlink.SuccessStatus;
import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUser;
import cloud.xcan.angus.api.commonlink.oauthuser.AuthenticationUserRepo;
import cloud.xcan.angus.api.commonlink.tenant.Tenant;
import cloud.xcan.angus.api.commonlink.user.User;
import cloud.xcan.angus.api.commonlink.user.UserSetting;
import cloud.xcan.angus.api.commonlink.user.enums.InviteStatus;
import cloud.xcan.angus.api.enums.SignInType;
import cloud.xcan.angus.core.biz.BizAssert;
import cloud.xcan.angus.core.biz.BizTemplate;
import cloud.xcan.angus.core.biz.ProtocolAssert;
import cloud.xcan.angus.core.biz.cmd.CommCmd;
import cloud.xcan.angus.core.biz.exception.BizException;
import cloud.xcan.angus.core.gm.application.cmd.authentication.AuthenticationUserCmd;
import cloud.xcan.angus.core.gm.application.cmd.email.EmailCmd;
import cloud.xcan.angus.core.gm.application.cmd.ldap.LdapCmd;
import cloud.xcan.angus.core.gm.application.cmd.log.UserOperationLogCmd;
import cloud.xcan.angus.core.gm.application.cmd.notification.NotificationHelperCmd;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.cmd.tenant.TenantCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.LoginDeviceCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.LoginHistoryCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserInviteCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserSecurityCmd;
import cloud.xcan.angus.core.gm.application.cmd.user.UserSettingCmd;
import cloud.xcan.angus.core.gm.application.converter.AuthorizationConverter;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationClientQuery;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationQuery;
import cloud.xcan.angus.core.gm.application.query.authentication.AuthenticationUserQuery;
import cloud.xcan.angus.core.gm.application.query.email.EmailTemplateQuery;
import cloud.xcan.angus.core.gm.application.query.security.SecurityQuery;
import cloud.xcan.angus.core.gm.application.query.sms.SmsTemplateQuery;
import cloud.xcan.angus.core.gm.application.query.user.LoginDeviceQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserInviteQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserSecurityQuery;
import cloud.xcan.angus.core.gm.application.query.user.UserSettingQuery;
import cloud.xcan.angus.core.gm.domain.email.Email;
import cloud.xcan.angus.core.gm.domain.log.OperationMessage;
import cloud.xcan.angus.core.gm.domain.log.enums.OperationAction;
import cloud.xcan.angus.core.gm.domain.log.enums.ResourceType;
import cloud.xcan.angus.core.gm.domain.notification.NotificationMessage;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationPriority;
import cloud.xcan.angus.core.gm.domain.notification.enums.NotificationType;
import cloud.xcan.angus.core.gm.domain.security.Security;
import cloud.xcan.angus.core.gm.domain.security.model.LoginSecurityConfig;
import cloud.xcan.angus.core.gm.domain.security.model.PasswordPolicyConfig;
import cloud.xcan.angus.core.gm.domain.security.model.RecipientUser;
import cloud.xcan.angus.core.gm.domain.security.model.SecurityNotificationConfig;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.domain.user.LoginDevice;
import cloud.xcan.angus.core.gm.domain.user.LoginHistory;
import cloud.xcan.angus.core.gm.domain.user.UserInvite;
import cloud.xcan.angus.core.gm.domain.user.UserSecurity;
import cloud.xcan.angus.core.gm.domain.user.enums.OAuthProvider;
import cloud.xcan.angus.core.gm.domain.user.enums.PasswordStrength;
import cloud.xcan.angus.core.gm.infra.authentication.OAuth2ProviderService;
import cloud.xcan.angus.core.gm.infra.authentication.OAuth2ProviderService.OAuthConfig;
import cloud.xcan.angus.core.gm.infra.authentication.OAuth2ProviderService.OAuthUserInfo;
import cloud.xcan.angus.core.gm.infra.utils.PasswordStrengthUtils;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.PasswordResetDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.RefreshTokenDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignInDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignupDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.VerificationCodeSendVo;
import cloud.xcan.angus.core.jpa.repository.BaseRepository;
import cloud.xcan.angus.core.utils.ValidatorUtils;
import cloud.xcan.angus.lettucex.util.RedisService;
import cloud.xcan.angus.remote.message.AbstractResultMessageException;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.SysException;
import cloud.xcan.angus.security.authentication.dao.DaoAuthenticationProvider;
import cloud.xcan.angus.security.client.CustomOAuth2RegisteredClient;
import cloud.xcan.angus.spec.principal.Principal;
import cloud.xcan.angus.spec.principal.PrincipalContext;
import jakarta.annotation.Resource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthenticationUserCmdImpl extends CommCmd<AuthenticationUser, Long>
    implements AuthenticationUserCmd {

  @Resource
  private AuthenticationQuery authenticationQuery;

  @Resource
  private AuthenticationUserQuery authenticationUserQuery;

  @Resource
  private AuthenticationClientQuery authenticationClientQuery;

  @Resource
  @Qualifier("authenticationUserRepo")
  private AuthenticationUserRepo authenticationUserRepo;

  @Resource
  private DaoAuthenticationProvider daoAuthenticationProvider;

  @Resource
  private OAuth2AuthorizationService oauth2AuthorizationService;

  @Resource
  private OAuth2ProviderService oauth2ProviderService;

  @Resource
  private SecurityQuery securityQuery;

  @Resource
  private PasswordEncoder passwordEncoder;

  @Resource
  private UserInviteQuery userInviteQuery;

  @Resource
  private UserInviteCmd userInviteCmd;

  @Resource
  private UserSettingQuery userSettingQuery;

  @Resource
  private UserSettingCmd userSettingCmd;

  @Resource
  private LdapCmd ldapCmd;

  @Resource
  private UserCmd userCmd;

  @Resource
  private UserQuery userQuery;

  @Resource
  private TenantCmd tenantCmd;

  @Resource
  private NotificationHelperCmd notificationHelperCmd;

  @Resource
  private SmsTemplateQuery smsTemplateQuery;

  @Resource
  private SmsCmd smsCmd;

  @Resource
  private EmailTemplateQuery emailTemplateQuery;

  @Resource
  private EmailCmd emailCmd;

  @Resource
  private LoginHistoryCmd loginHistoryCmd;

  @Resource
  private LoginDeviceQuery loginDeviceQuery;

  @Resource
  private LoginDeviceCmd loginDeviceCmd;

  @Resource
  private UserSecurityCmd userSecurityCmd;

  @Resource
  private UserSecurityQuery userSecurityQuery;

  @Resource
  private UserOperationLogCmd userOperationLogCmd;

  @Resource
  private AuthenticationUserCmd self;

  // TODO 替换成数据库实现
  @Resource
  private RedisService<String> stringRedisService;

  @Override
  public void create0(AuthenticationUser authUser) {
    insert0(authUser);
  }

  @Override
  public void update0(AuthenticationUser authUser) {
    authenticationUserRepo.save(authUser);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public User signIn(UserSignInDto dto) {
    return new BizTemplate<User>(false) {
      AuthenticationUser authUserDb;
      final Principal principal = PrincipalContext.get();

      @Override
      protected void checkParams() {
        // 验证设备ID
        assertTrue(nonNull(principal.getDeviceInfo())
                && isNotEmpty(principal.getDeviceInfo().getDeviceId()),
            LOGIN_DEVICE_ID_EMPTY, PARAM_MISSING_KEY);

        // 验证登录账号非空
        assertTrue(isNotBlank(dto.getAccount()), LOGIN_ACCOUNT_EMPTY, PARAM_MISSING_KEY);

        // 验证客户端
        authenticationClientQuery.checkAndFind(dto.getClientId(), dto.getClientSecret(),
            dto.getScope());

        // 验证登录类型和参数
        if (SignInType.ACCOUNT_PASSWORD.equals(dto.getLoginType())) {
          // 账号密码登录
          assertTrue(isNotBlank(dto.getPassword()), LOGIN_ACCOUNT_EMPTY, PARAM_MISSING_KEY);

          // 验证图形验证码
          if (isNotBlank(dto.getCaptchaKey()) && isNotBlank(dto.getCaptcha())) {
            assertTrue(authenticationQuery.verifyCaptcha(dto.getCaptchaKey(), dto.getCaptcha()),
                "验证码错误或已过期");
          }

          // 查找用户：如果指定了userId，优先使用userId查找；否则使用用户名查找
          if (dto.getUserId() != null) {
            authUserDb = authenticationUserQuery.findAndCheck(dto.getUserId());
            // 验证用户名是否匹配
            assertTrue(dto.getAccount().equals(authUserDb.getUsername())
                    || dto.getAccount().equals(authUserDb.getEmail())
                    || dto.getAccount().equals(authUserDb.getPhone()),
                "用户名与指定用户ID不匹配");

            // 验证密码
            authenticationUserQuery.checkPassword(Long.valueOf(authUserDb.getId()),
                dto.getPassword());

          } else {
            List<AuthenticationUser> authUsers = authenticationUserQuery.findByAccountAndPassword(
                dto.getAccount(), dto.getPassword());
            if (isEmpty(authUsers) && !isCloudServiceEdition()) {
              try {
                ldapCmd.syncAllEnabledUsers();
                authUsers = authenticationUserQuery.findByAccountAndPassword(
                    dto.getAccount(), dto.getPassword());
              } catch (Exception e) {
                log.debug("LDAP sync failed or no enabled configs before retry: {}",
                    e.getMessage());
              }
            }
            assertTrue(authUsers != null && !authUsers.isEmpty(), "用户不存在或密码错误");
            authUserDb = authUsers.get(0);
          }
        } else if (SignInType.SMS_CODE.equals(dto.getLoginType())) {
          // 短信验证码登录
          assertTrue(isNotBlank(dto.getCode()), SMS_VERIFY_CODE_EMPTY, PARAM_MISSING_KEY);
          assertTrue(isNotBlank(dto.getCodeKey()), SMS_VERIFY_CODE_EMPTY, PARAM_MISSING_KEY);

          // 验证手机号用户是否存在
          List<AuthenticationUser> authUsers = checkMobileUserExist(dto.getAccount());
          // 查找用户：如果指定了userId，优先使用userId查找；否则从列表中取第一个
          if (dto.getUserId() != null) {
            authUserDb = authUsers.stream()
                .filter(u -> Long.parseLong(u.getId()) == dto.getUserId())
                .findFirst().orElseThrow(() -> ProtocolException.of("用户ID与手机号不匹配"));
          } else {
            authUserDb = authUsers.get(0);
          }

          // 验证短信验证码
          checkSignInSmsCode(TEMPLATE_CODE_VERIFICATION_CODE, dto.getAccount(), dto.getCode(),
              dto.getCodeKey(), authUserDb);
        } else if (SignInType.EMAIL_CODE.equals(dto.getLoginType())) {
          // 邮箱验证码登录
          assertTrue(isNotBlank(dto.getCode()), EMAIL_VERIFY_CODE_EMPTY, PARAM_MISSING_KEY);
          assertTrue(isNotBlank(dto.getCodeKey()), EMAIL_VERIFY_CODE_EMPTY, PARAM_MISSING_KEY);

          // 验证邮箱用户是否存在
          List<AuthenticationUser> authUsers = checkEmailUserExist(dto.getAccount());
          // 查找用户：如果指定了userId，优先使用userId查找；否则从列表中取第一个
          if (dto.getUserId() != null) {
            authUserDb = authUsers.stream()
                .filter(u -> Long.parseLong(u.getId()) == dto.getUserId())
                .findFirst().orElseThrow(() -> ProtocolException.of("用户ID与邮箱不匹配"));
          } else {
            authUserDb = authUsers.get(0);
          }

          // 验证邮箱验证码
          checkSignInEmailCode(TEMPLATE_CODE_LOGIN_VERIFICATION, dto.getAccount(), dto.getCode(),
              dto.getCodeKey(), authUserDb);
        } else {
          throw ProtocolException.of("不支持的登录类型");
        }

        // 验证用户状态
        authenticationUserQuery.checkUserValid(authUserDb);
      }

      @Override
      protected User process() {
        try {
          // 设置登录用户上下文
          principal.setClientId(dto.getClientId())
              .setUserId(valueOf(authUserDb.getId()))
              .setFullName(authUserDb.getFullName())
              .setTenantId(valueOf(authUserDb.getTenantId()))
              .setTenantName(authUserDb.getTenantName());

          // 检查登录尝试次数
          checkLoginPasswordErrors(authUserDb.getUsername());

          // 缓存用户信息供认证提供者使用
          daoAuthenticationProvider.getUserCache().putUserInCache(
              authUserDb.getId(), authUserDb.getUsername(), AuthenticationUser.with(authUserDb));

          // 根据登录类型获取账号和密码
          String account = dto.getAccount();
          String password = SignInType.ACCOUNT_PASSWORD.equals(dto.getLoginType())
              ? dto.getPassword() : authUserDb.getLinkSecret();

          // 提交OAuth2认证请求生成token
          Map<String, String> result = submitOauth2UserLoginRequest(
              dto.getClientId(), dto.getClientSecret(), dto.getLoginType(),
              String.valueOf(authUserDb.getId()), account, password, dto.getScope());

          // 获取用户详细信息
          User user = userQuery.findAndCheck(Long.valueOf(authUserDb.getId()));
          user.setTokenResult(result);

          // 记录登录设备和登录历史日志
          recordLoginDeviceAndHistory(Long.valueOf(authUserDb.getId()), authUserDb.getUsername(),
              dto.getLoginType(), SuccessStatus.SUCCESS, null);

          // 更新用户最后登录信息
          PasswordStrength passwordStrength
              = SignInType.ACCOUNT_PASSWORD.equals(dto.getLoginType()) ?
              PasswordStrengthUtils.calculatePasswordStrength(dto.getPassword()) : null;
          userSecurityCmd.updateLastLogin(user.getId(), principal, LocalDateTime.now(), null,
              passwordStrength);
          return user;
        } catch (Throwable e) {
          // 记录登录设备和登录历史日志
          recordLoginDeviceAndHistory(Long.valueOf(authUserDb.getId()), authUserDb.getUsername(),
              dto.getLoginType(), SuccessStatus.FAILED, e.getMessage());

          if (e instanceof AbstractResultMessageException) {
            throw (AbstractResultMessageException) e;
          }
          throw new SysException(e.getMessage());
        }
      }

      @Override
      protected void onCustomException(AbstractResultMessageException e) {
        if (authUserDb != null) {
          // 记录失败的登录尝试
          Security security = securityQuery.getLoginSecurityConfig();
          int passwordErrors = recordLoginInPasswordErrors(authUserDb.getUsername(), security);

          // 如果登录密码错误超过最大限制
          LoginSecurityConfig config = (LoginSecurityConfig) security.getConfig();
          if (passwordErrors >= 3 || passwordErrors >= config.getMaxLoginAttempts()) {
            // 在新事务中保存通知，确保通知能够独立提交
            self.createLoginFailedNotificationInNewTransaction(authUserDb.getUsername(),
                passwordErrors);
          }
        }
        // 最后抛出异常向上提示
        throw e;
      }
    }.execute();
  }

  /**
   * OAuth第三方登录
   *
   * <p>完整流程说明：
   * <ol>
   *   <li><b>参数验证阶段（checkParams）</b>：
   *     <ul>
   *       <li>验证设备ID、provider和code参数</li>
   *       <li>获取OAuth配置（从系统配置中读取对应提供商的配置信息）</li>
   *       <li>使用授权码code换取access_token（调用第三方OAuth服务）</li>
   *       <li>使用access_token获取第三方用户信息（调用第三方用户信息接口）</li>
   *       <li>根据第三方用户ID（openId）查找本地用户（通过SettingUser中的第三方账号绑定字段关联）</li>
   *     </ul>
   *   </li>
   *   <li><b>业务处理阶段（process）</b>：
   *     <ul>
   *       <li><b>新用户场景</b>：
   *         <ol>
   *           <li>生成唯一的用户名（格式：provider_openId，如已存在则添加后缀）</li>
   *           <li>创建User实体（设置用户名、昵称、邮箱、头像，生成随机密码）</li>
   *           <li>创建AuthUser实体（用于OAuth2认证，设置认证相关字段）</li>
   *         </ol>
   *       </li>
   *       <li><b>已存在用户场景</b>：
   *         <ol>
   *           <li>获取现有User和AuthUser信息</li>
   *           <li>更新用户信息（头像、昵称、邮箱等可能会变化的信息）</li>
   *         </ol>
   *       </li>
   *       <li><b>统一处理</b>：
   *         <ol>
   *           <li>更新或创建第三方账号绑定关系（记录到SettingUser的独立字段中）</li>
   *           <li>设置登录用户上下文（PrincipalContext）</li>
   *           <li>缓存用户信息供认证提供者使用</li>
   *           <li>提交OAuth2认证请求生成token（使用ACCOUNT_PASSWORD类型）</li>
   *           <li>从token中解析userId并获取用户详细信息</li>
   *           <li>转换为LoginVo并返回（包含accessToken、refreshToken、用户信息等）</li>
   *         </ol>
   *       </li>
   *     </ul>
   *   </li>
   * </ol>
   */
  @Override
  @Transactional(rollbackFor = Exception.class)
  public User socialSignIn(OAuthProvider provider, String code, String state) {
    return new BizTemplate<User>(false) {
      OAuthUserInfo oauthUserInfo;
      final Principal principal = PrincipalContext.get();

      @Override
      protected void checkParams() {
        // 验证设备ID
        assertTrue(nonNull(principal.getDeviceInfo())
                && isNotEmpty(principal.getDeviceInfo().getDeviceId()),
            LOGIN_DEVICE_ID_EMPTY, PARAM_MISSING_KEY);

        // 验证provider和code参数
        assertTrue(provider != null, QUERY_FIELD_EMPTY_T, new Object[]{"provider"});
        assertTrue(isNotBlank(code), QUERY_FIELD_EMPTY_T, new Object[]{"code"});

        // 获取OAuth配置（从系统配置中读取对应提供商的配置信息）
        OAuthConfig oauthConfig = oauth2ProviderService.getOAuthConfig(provider);

        // 使用授权码code换取access_token（调用第三方OAuth服务）
        String oauthAccessToken = oauth2ProviderService.exchangeAccessToken(provider,
            code, oauthConfig);

        // 使用access_token获取第三方用户信息（调用第三方用户信息接口）
        oauthUserInfo = oauth2ProviderService.getUserInfo(provider, oauthAccessToken, oauthConfig);
        assertTrue(oauthUserInfo != null && isNotBlank(oauthUserInfo.getOpenId()),
            "获取第三方用户信息失败");

      }

      @Override
      protected User process() {
        // 根据第三方用户ID（openId）查找本地用户（通过SettingUser中的第三方账号绑定字段关联）
        UserSetting setting = userSettingQuery.findUserByOAuthId(provider,
            oauthUserInfo.getOpenId());

        try {
          // 处理用户创建或更新
          User user = createOrUpdateUser(setting);

          // 更新或创建第三方账号绑定关系（记录到SettingUser的独立字段中）
          userSettingCmd.updateSocialBinding(user.getId(), provider, oauthUserInfo.getOpenId());

          // 获取用于生成token的AuthUser, AuthClient
          AuthenticationUser authUser = user.getAuthUser();
          CustomOAuth2RegisteredClient authClient = authenticationClientQuery.checkAndFind(
              XCAN_TENANT_PLATFORM_CODE);

          // 设置登录用户上下文
          principal.setClientId(authClient.getClientId())
              .setUserId(user.getId())
              .setFullName(authUser.getFullName())
              .setTenantId(valueOf(authUser.getTenantId()))
              .setTenantName(authUser.getTenantName());

          // 缓存用户信息供认证提供者使用
          daoAuthenticationProvider.getUserCache().putUserInCache(
              authUser.getId(), authUser.getUsername(), AuthenticationUser.with(authUser));

          // 提交OAuth2认证请求生成token（使用ACCOUNT_PASSWORD类型，因为OAuth用户已经有本地账号）
          Map<String, String> tokenResult = submitOauth2UserLoginRequest(
              authClient.getClientId(), authClient.getClientSecret(), SignInType.ACCOUNT_PASSWORD,
              String.valueOf(authUser.getId()), authUser.getUsername(),
              authUser.getPassword(), USER_TOKEN_CLIENT_SCOPE);
          user.setTokenResult(tokenResult);

          // 记录登录设备和登录历史日志
          recordLoginDeviceAndHistory(user.getId(), authUser.getUsername(),
              SignInType.ACCOUNT_PASSWORD, SuccessStatus.SUCCESS, null);

          // 更新用户最后登录信息
          userSecurityCmd.updateLastLogin(user.getId(), principal, LocalDateTime.now(), null, null);
          return user;
        } catch (Throwable e) {
          try {
            if (setting != null) {
              // 记录登录设备和登录历史日志（如果用户已存在）
              AuthenticationUser authUser = authenticationUserQuery.findAndCheck(
                  setting.getUserId());
              recordLoginDeviceAndHistory(setting.getUserId(), authUser.getUsername(),
                  SignInType.ACCOUNT_PASSWORD, SuccessStatus.FAILED, e.getMessage());
            }

            // 通知用户表前1个用户，用户三方登录失败信息
            if (oauthUserInfo != null) {
              String providerName = getProviderDisplayName(provider);
              String openId = oauthUserInfo.getOpenId();
              String errorMessage = e.getMessage() != null ? e.getMessage() : "未知错误";
              // 在新事务中保存通知，确保通知能够独立提交
              self.createOAuthLoginFailedNotificationInNewTransaction(
                  providerName, openId, errorMessage);
            }
          } catch (Exception ex) {
            // 忽略记录失败，不影响主流程
          }

          if (e instanceof AbstractResultMessageException) {
            throw (AbstractResultMessageException) e;
          }
          throw new SysException("OAuth登录失败: " + e.getMessage(), e);
        }
      }

      /**
       * 创建或更新用户
       */
      private User createOrUpdateUser(UserSetting setting) {
        User user;
        if (setting == null) {
          // 新用户场景：创建新用户
          user = createUser(provider, oauthUserInfo);
        } else {
          // 已存在用户场景：更新用户信息
          user = updateExistingOAuthUser(setting.getUserId(), oauthUserInfo);
        }
        return user;
      }

      /**
       * 创建新的用户
       */
      private User createUser(OAuthProvider provider,
          OAuthUserInfo oauthUserInfo) {
        // 生成唯一的用户名（格式：provider_openId，如已存在则添加后缀）
        String username = generateOAuthUsername(provider, oauthUserInfo.getOpenId());

        // 创建User实体（设置用户名、昵称、邮箱、头像，生成随机密码）
        User user = AuthorizationConverter.toUserFromOAuth(username, oauthUserInfo);

        // 创建用户
        return userCmd.create0(user);
      }

      /**
       * 更新已存在的用户信息
       */
      private User updateExistingOAuthUser(Long userId, OAuthUserInfo oauthUserInfo) {
        // 获取现有用户信息
        AuthenticationUser existingAuthUser = authenticationUserQuery.findAndCheck(userId);
        User user = userQuery.findAndCheck(userId);

        // 更新用户信息（头像、昵称、邮箱等可能会变化的信息）
        AuthorizationConverter.updateUserFromOAuth(user, oauthUserInfo);
        userCmd.update(user);

        // 更新AuthUser信息
        AuthorizationConverter.updateAuthUserFromUser(existingAuthUser, oauthUserInfo);
        authenticationUserRepo.save(existingAuthUser);

        // 关联认证用户到User实体
        user.setAuthUser(existingAuthUser);
        return user;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public User signUp(UserSignupDto dto) {
    return new BizTemplate<User>() {
      UserInvite userInvite;
      final Principal principal = PrincipalContext.get();

      @Override
      protected void checkParams() {
        // 邀请码注册：验证邀请码（如果提供），邀请码注册不受 allowRegistrationEnabled 限制
        if (!isBlank(dto.getInviteCode())) {
          userInvite = userInviteQuery.findAndCheck(dto.getInviteCode());
        } else {
          // 开放注册（无邀请码）：仅云服务版和数据中心版可开启，且需在安全配置中启用
          String editionType = getApplicationInfo().getEditionType();
          boolean isRegistrationAllowedEdition = EditionType.CLOUD_SERVICE.name().equals(editionType)
              || EditionType.DATACENTER.name().equals(editionType);
          if (!isRegistrationAllowedEdition) {
            throw ProtocolException.of("当前版本不支持开放注册，仅云服务版和数据中心版可开启");
          }
          Object config = securityQuery.getLoginSecurityConfig().getConfig();
          LoginSecurityConfig loginConfig = config instanceof LoginSecurityConfig
              ? (LoginSecurityConfig) config : null;
          if (loginConfig == null || !Boolean.TRUE.equals(loginConfig.getAllowRegistrationEnabled())) {
            throw ProtocolException.of("注册功能已关闭，请联系管理员");
          }
        }

        // 验证设备ID
        assertTrue(nonNull(principal.getDeviceInfo())
                && isNotEmpty(principal.getDeviceInfo().getDeviceId()),
            LOGIN_DEVICE_ID_EMPTY, PARAM_MISSING_KEY);

        // 验证密码
        assertTrue(isNotBlank(dto.getPassword()), QUERY_FIELD_EMPTY_T, new Object[]{"password"});
        assertTrue(dto.getPassword().equals(dto.getConfirmPassword()), "密码和确认密码不一致");

        // 验证协议同意
        assertTrue(Boolean.TRUE.equals(dto.getAgreement()), "必须同意用户协议");

        // 开放注册（无邀请码）- 验证验证码和手机/邮箱
        if (userInvite == null) {
          // 验证验证码非空
          assertTrue(isNotBlank(dto.getCode()), QUERY_FIELD_EMPTY_T, new Object[]{"code"});

          // 验证注册类型和参数
          if (SignInType.SMS_CODE.equals(dto.getRegisterType())) {
            assertTrue(isNotBlank(dto.getPhone()), QUERY_FIELD_EMPTY_T, new Object[]{"phone"});
            ValidatorUtils.checkMobile("CN", dto.getPhone());

            // 验证短信验证码
            checkSmsVerificationCode(TEMPLATE_CODE_VERIFICATION_CODE, dto.getPhone(),
                dto.getCode(), dto.getCodeKey());
          } else if (SignInType.EMAIL_CODE.equals(dto.getRegisterType())) {
            assertTrue(isNotBlank(dto.getEmail()), QUERY_FIELD_EMPTY_T, new Object[]{"email"});
            ValidatorUtils.checkEmail(dto.getEmail());

            // 验证邮箱验证码
            checkEmailVerificationCode(REGISTER_VERIFICATION_TEMPLATE_CODE, dto.getEmail(),
                dto.getCode(), dto.getCodeKey());
          } else {
            throw ProtocolException.of("不支持的注册类型");
          }
        }
      }

      @Override
      protected User process() {
        // 创建并保存新租户信息
        Long tenantId = initTenant();
        PrincipalContext.get().setTenantId(tenantId);

        // 创建用户
        User user = toSignUpUser(dto, userInvite, tenantId);
        User createdUser = userCmd.create0(user);
        PrincipalContext.get().setUserId(user.getId());

        // 更新邀请状态（如果使用了邀请码）
        if (userInvite != null) {
          if (isNotEmpty(userInvite.getEmail())) {
            userInvite.setEmail(user.getEmail());
          }
          userInvite.setStatus(InviteStatus.ACCEPTED);
          userInviteCmd.update0(userInvite);

          // 发送注册成功通知给邀请人
          if (userInvite.getInvitedBy() != null) {
            String userName = createdUser.getName() != null
                ? createdUser.getName() : createdUser.getUsername();
            notificationHelperCmd.createByMessageKey(
                NotificationType.SUCCESS,
                NotificationMessage.USER_REGISTER_SUCCESS_TITLE,
                NotificationMessage.USER_REGISTER_SUCCESS_DESCRIPTION,
                NotificationMessage.CATEGORY_USER_MANAGEMENT,
                NotificationPriority.MEDIUM,
                userInvite.getInvitedBy(),
                new Object[]{userName},
                new Object[]{userName}
            );
          }
        } else {
          // 非邀请注册，通知 recipientUsers 中配置的用户
          List<Long> userIds = getNotificationRecipientUserIds(false, /* newUserRegisterNotify */
              true);
          if (userIds != null && !userIds.isEmpty()) {
            String userName = createdUser.getName() != null
                ? createdUser.getName() : createdUser.getUsername();
            notificationHelperCmd.createBatchByMessageKey(
                NotificationType.SUCCESS,
                NotificationMessage.USER_REGISTER_SUCCESS_TITLE,
                NotificationMessage.USER_REGISTER_SUCCESS_DESCRIPTION,
                NotificationMessage.CATEGORY_USER_MANAGEMENT,
                NotificationPriority.MEDIUM,
                userIds,
                new Object[]{userName},
                new Object[]{userName}
            );
          }
        }
        // 返回完整用户信息
        return createdUser;
      }

      private Long initTenant() {
        Long tenantId;
        if (userInvite == null) {
          Tenant newTenant = toSignUpTenant(dto);
          tenantCmd.create(newTenant);
          tenantId = newTenant.getId();
        } else {
          tenantId = userInvite.getTenantId();
        }
        return tenantId;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void resetPassword(PasswordResetDto dto) {
    new BizTemplate<Void>() {
      AuthenticationUser authUserDb;
      final Principal principal = PrincipalContext.get();

      @Override
      protected void checkParams() {
        // 验证验证码非空
        assertTrue(isNotBlank(dto.getCode()), QUERY_FIELD_EMPTY_T, new Object[]{"code"});

        // 验证密码
        assertTrue(isNotBlank(dto.getNewPassword()), QUERY_FIELD_EMPTY_T,
            new Object[]{"newPassword"});
        assertTrue(dto.getNewPassword().equals(dto.getConfirmPassword()), "密码和确认密码不一致");

        // 验证重置类型和参数
        if (SignInType.SMS_CODE.equals(dto.getType())) {
          assertTrue(isNotBlank(dto.getAccount()), QUERY_FIELD_EMPTY_T, new Object[]{"account"});

          // 验证短信验证码
          checkSmsVerificationCode(TEMPLATE_CODE_VERIFICATION_CODE, dto.getAccount(),
              dto.getCode(), dto.getCodeKey());

          // 查找用户
          List<AuthenticationUser> users = authenticationUserQuery.findByMobile(dto.getAccount())
              .stream()
              .filter(x -> dto.getUserId() == null || x.getId().equals(dto.getUserId().toString()))
              .toList();
          assertTrue(!users.isEmpty(), "用户不存在");
          authUserDb = users.get(0);

        } else if (SignInType.EMAIL_CODE.equals(dto.getType())) {
          assertTrue(isNotBlank(dto.getAccount()), QUERY_FIELD_EMPTY_T, new Object[]{"account"});

          // 验证邮箱验证码
          checkEmailVerificationCode(TEMPLATE_CODE_RETRIEVE_PASSWORD_VERIFICATION,
              dto.getAccount(), dto.getCode(), dto.getCodeKey());

          // 查找用户
          List<AuthenticationUser> users = authenticationUserQuery.findByEmail(dto.getAccount())
              .stream()
              .filter(x -> dto.getUserId() == null || x.getId().equals(dto.getUserId().toString()))
              .toList();
          assertTrue(!users.isEmpty(), "用户不存在");
          authUserDb = users.get(0);
        } else {
          throw ProtocolException.of("不支持的重置密码类型");
        }

        // 验证用户状态
        authenticationUserQuery.checkUserValid(authUserDb);

        // 验证密码强度（包括长度、字符要求等）
        securityQuery.validatePasswordByPolicy(dto.getNewPassword());

        // 检查历史密码重复
        Long userId = valueOf(authUserDb.getId());
        authenticationUserQuery.checkHistoryPasswordExists(userId, dto.getNewPassword());

        // 检查新密码是否与当前密码相同
        if (passwordEncoder.matches(dto.getNewPassword(), authUserDb.getPassword())) {
          throw ProtocolException.of("新密码不能与当前密码相同");
        }
      }

      @Override
      protected Void process() {
        // 更新密码
        String newPasswordEncoded = passwordEncoder.encode(dto.getNewPassword());
        authUserDb.setPassword(newPasswordEncoded);
        authUserDb.setLastModifiedPasswordDate(Instant.now());
        authenticationUserRepo.save(authUserDb);

        // 更新历史密码
        Long userId = valueOf(authUserDb.getId());
        UserSecurity userSecurity = userSecurityQuery.findOrCreateByUserId(userId);
        Security security = securityQuery.getPasswordPolicy();
        PasswordPolicyConfig config = security.getConfig() instanceof PasswordPolicyConfig
            ? (PasswordPolicyConfig) security.getConfig()
            : new PasswordPolicyConfig();
        userSecurityCmd.savePasswordHistory(config, userSecurity, newPasswordEncoded);

        // 记录操作日志
        String userName =
            authUserDb.getFullName() != null ? authUserDb.getFullName() : authUserDb.getUsername();
        String account = dto.getAccount() != null ? dto.getAccount() : "";
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            userId,
            userName,
            OperationMessage.USER_RESET_PASSWORD_DETAILS,
            new Object[]{userName, account}
        );

        principal.setUserId(userId)
            .setFullName(authUserDb.getFullName())
            .setTenantId(valueOf(authUserDb.getTenantId()))
            .setTenantName(authUserDb.getTenantName());
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void changePassword(Long id, String oldPassword, String newPassword,
      String confirmPassword) {
    new BizTemplate<Void>() {
      AuthenticationUser authUserDb;

      @Override
      protected void checkParams() {
        // 查询并验证用户存在
        authUserDb = authenticationUserRepo.findById(id).orElseThrow(() ->
            ProtocolException.of("认证用户不存在，无法修改密码"));
        // 验证新密码与确认密码匹配
        if (!newPassword.equals(confirmPassword)) {
          throw ProtocolException.of("新密码与确认密码不匹配");
        }
        // 验证旧密码是否正确
        if (!passwordEncoder.matches(oldPassword, authUserDb.getPassword())) {
          throw ProtocolException.of("原密码错误");
        }
        // 确保新旧密码不同
        if (newPassword.equals(oldPassword)) {
          throw ProtocolException.of("新密码不能与原密码相同");
        }
        // 验证密码强度（包括长度、字符要求等）
        securityQuery.validatePasswordByPolicy(newPassword);

        // 检查历史密码重复
        authenticationUserQuery.checkHistoryPasswordExists(id, newPassword);
      }

      @Override
      protected Void process() {
        // 更新加密密码
        String newPasswordEncoded = passwordEncoder.encode(newPassword);
        authUserDb.setPassword(newPasswordEncoded);
        authUserDb.setLastModifiedPasswordDate(Instant.now());
        authenticationUserRepo.save(authUserDb);

        // 更新历史密码
        UserSecurity userSecurity = userSecurityQuery.findOrCreateByUserId(id);
        Security security = securityQuery.getPasswordPolicy();
        PasswordPolicyConfig config = security.getConfig() instanceof PasswordPolicyConfig
            ? (PasswordPolicyConfig) security.getConfig()
            : new PasswordPolicyConfig();
        userSecurityCmd.savePasswordHistory(config, userSecurity, newPasswordEncoded);

        // 记录操作日志
        String userName =
            authUserDb.getFullName() != null ? authUserDb.getFullName() : authUserDb.getUsername();
        userOperationLogCmd.logSuccessByMessageKey(
            OperationAction.UPDATE,
            ResourceType.USER,
            id,
            userName,
            OperationMessage.USER_CHANGE_PASSWORD_DETAILS,
            new Object[]{userName}
        );

        return null;
      }
    }.execute();
  }

  @Override
  public User refreshToken(RefreshTokenDto dto) {
    return new BizTemplate<User>(false) {
      @Override
      protected void checkParams() {
        // 验证客户端
        authenticationClientQuery.checkAndFind(dto.getClientId(), dto.getClientSecret());
      }

      @Override
      protected User process() {
        try {
          // 提交OAuth2刷新token请求
          Map<String, String> tokenResult = submitOauth2RenewRequest(dto.getClientId(),
              dto.getClientSecret(), dto.getRefreshToken());

          // 从token中解析userId并获取用户详细信息
          String accessToken = tokenResult.get(ACCESS_TOKEN);
          AuthenticationUser authUser = authenticationQuery.findByToken(accessToken);
          if (authUser == null) {
            throw ProtocolException.of("无效的访问令牌或用户已被删除");
          }
          User user = userQuery.findAndCheck(Long.valueOf(authUser.getId()));
          user.setTokenResult(tokenResult);
          return user;
        } catch (Throwable e) {
          if (e instanceof AbstractResultMessageException) {
            throw (AbstractResultMessageException) e;
          }
          throw new SysException(e.getMessage());
        }
      }
    }.execute();
  }

  @Override
  public void logout(String accessToken) {
    new BizTemplate<Void>() {
      OAuth2Authorization authorizationDb;
      CustomOAuth2RegisteredClient clientDb;

      @Override
      protected void checkParams() {
        ProtocolAssert.assertNotEmpty(accessToken, "accessToken 不能为空");

        // 查找授权信息
        authorizationDb = oauth2AuthorizationService.findByToken(accessToken,
            OAuth2TokenType.ACCESS_TOKEN);

        // 验证客户端
        clientDb = authenticationClientQuery.checkAndFind(authorizationDb.getRegisteredClientId());

        if (nonNull(authorizationDb)) {
          // 确保只有用户登录token才能退出
          assertTrue(isUserSignIn(clientDb.getSource()), "只有用户登录token才能退出");
        }
      }

      @Override
      protected Void process() {
        if (isNull(authorizationDb)) {
          return null;
        }

        // 移除OAuth2授权
        oauth2AuthorizationService.remove(authorizationDb);

        // 设置用户为下线状态
        userCmd.updateOfflineStatusByUsername(authorizationDb.getPrincipalName());
        return null;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public VerificationCodeSendVo sendSmsCode(String templateCode, Language language, String phone) {
    return new BizTemplate<VerificationCodeSendVo>() {

      @Override
      protected void checkParams() {
        smsTemplateQuery.findAndCheck(templateCode, language);
      }

      @Override
      protected VerificationCodeSendVo process() {
        // 作为链接密钥
        String codeKey = "SMS-" + randomAlphabetic(MAX_LINK_SECRET_LENGTH);

        // 发送短信验证码
        Map<String, String> params = new HashMap<>();
        String verificationCode = RandomStringUtils.randomNumeric(6);
        params.put(VERIFICATION_CODE_NAME, verificationCode);
        LoginSecurityConfig securityConfig
            = (LoginSecurityConfig) securityQuery.getLoginSecurityConfig().getConfig();
        params.put(VERIFICATION_CODE_VALID_MINUTE,
            String.valueOf(valueOf(securityConfig.getCodeExpiration() / 60)));
        Sms sms = smsCmd.send(templateCode, language, phone, params);
        if (sms.getStatus().isFailed()) {
          throw ProtocolException.of(sms.getErrorMessage());
        }

        // TODO 通过数据库记录验证码到期时间，通过数据库验证到期
        cacheSmsVerificationCode(templateCode, phone,
            verificationCode, securityConfig.getCodeExpiration(), codeKey);

        // 生成验证码key（实际应该从短信服务返回）
        VerificationCodeSendVo vo = new VerificationCodeSendVo();
        vo.setCodeKey(codeKey);
        vo.setExpireTime(securityConfig.getCodeExpiration()); // 默认5分钟过期
        return vo;
      }
    }.execute();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public VerificationCodeSendVo sendEmailCode(String templateCode, Language language,
      String email) {
    return new BizTemplate<VerificationCodeSendVo>() {
      @Override
      protected void checkParams() {
        emailTemplateQuery.findAndCheckValid(templateCode, language);
      }

      @Override
      protected VerificationCodeSendVo process() {
        String codeKey = "EMAIL-" + randomAlphabetic(MAX_LINK_SECRET_LENGTH);

        // 发送邮件验证码
        Map<String, String> params = new HashMap<>();
        String verificationCode = RandomStringUtils.randomNumeric(6);
        params.put(VERIFICATION_CODE_NAME, verificationCode);
        LoginSecurityConfig securityConfig
            = (LoginSecurityConfig) securityQuery.getLoginSecurityConfig().getConfig();
        params.put(VERIFICATION_CODE_VALID_MINUTE,
            String.valueOf(valueOf(securityConfig.getCodeExpiration() / 60)));
        Email sentEmail = emailCmd.sendByTemplate(templateCode, language,
            email, null, null, params, false);
        if (sentEmail.getStatus().isFailed()) {
          throw ProtocolException.of(sentEmail.getErrorMessage());
        }

        // TODO 通过数据库记录验证码到期时间，通过数据库验证到期
        cacheEmailVerificationCode(templateCode, email,
            verificationCode, securityConfig.getCodeExpiration(), codeKey);

        // 生成验证码key（实际应该从短信服务返回）
        VerificationCodeSendVo vo = new VerificationCodeSendVo();
        vo.setCodeKey(codeKey);
        vo.setExpireTime(securityConfig.getCodeExpiration()); // 默认5分钟过期
        return vo;
      }
    }.execute();
  }

  @Override
  public void deleteById(Long id) {
    authenticationUserRepo.deleteById(String.valueOf(id));
  }

  @Override
  public void deleteByTenantId(Long tenantId) {
    authenticationUserRepo.deleteByTenantId(String.valueOf(tenantId));
  }

  /**
   * 在新事务中创建登录失败通知
   * <p>使用 REQUIRES_NEW 传播级别，确保通知保存操作在独立事务中执行，
   * 即使主事务回滚，通知也能成功保存
   *
   * @param username       用户名
   * @param passwordErrors 密码错误次数
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void createLoginFailedNotificationInNewTransaction(String username, int passwordErrors) {
    List<Long> userIds = getNotificationRecipientUserIds(/* loginFailureNotify */ true, false);
    if (userIds != null && !userIds.isEmpty()) {
      notificationHelperCmd.createBatchByMessageKey(
          NotificationType.WARNING,
          NotificationMessage.USER_LOGIN_FAILED_TITLE,
          NotificationMessage.USER_LOGIN_FAILED_DESCRIPTION,
          NotificationMessage.CATEGORY_USER_MANAGEMENT,
          NotificationPriority.HIGH,
          userIds,
          new Object[]{username},
          new Object[]{username, passwordErrors}
      );
    }
  }

  /**
   * 在新事务中创建OAuth登录失败通知
   * <p>使用编程式事务管理，确保通知保存操作在独立事务中执行，
   * 即使主事务回滚，通知也能成功保存。使用编程式事务可以避免Spring AOP代理自调用问题。
   *
   * @param providerName 第三方登录提供商名称
   * @param openId       第三方用户ID
   * @param errorMessage 错误消息
   */
  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
  public void createOAuthLoginFailedNotificationInNewTransaction(
      String providerName, String openId, String errorMessage) {
    List<Long> userIds = getNotificationRecipientUserIds(/* loginFailureNotify */ true, false);
    if (userIds != null && !userIds.isEmpty()) {
      notificationHelperCmd.createBatchByMessageKey(
          NotificationType.WARNING,
          NotificationMessage.USER_OAUTH_LOGIN_FAILED_TITLE,
          NotificationMessage.USER_OAUTH_LOGIN_FAILED_DESCRIPTION,
          NotificationMessage.CATEGORY_USER_MANAGEMENT,
          NotificationPriority.HIGH,
          userIds,
          new Object[]{providerName},
          new Object[]{providerName, openId, errorMessage}
      );
    }
  }

  /**
   * 根据安全通知配置获取接收用户ID列表
   *
   * @param loginFailureNotify    是否检查登录失败通知开关
   * @param newUserRegisterNotify 是否检查新用户注册通知开关
   * @return 用户ID列表，未开启或配置为空时返回 null
   */
  private List<Long> getNotificationRecipientUserIds(boolean loginFailureNotify,
      boolean newUserRegisterNotify) {
    Security security = securityQuery.getNotificationConfig();
    if (security == null || !(security.getConfig() instanceof SecurityNotificationConfig config)) {
      return null;
    }
    if (loginFailureNotify && !Boolean.TRUE.equals(config.getLoginFailureNotify())) {
      return null;
    }
    if (newUserRegisterNotify && !Boolean.TRUE.equals(config.getNewUserRegisterNotify())) {
      return null;
    }
    List<RecipientUser> recipientUsers = config.getRecipientUsers();
    if (recipientUsers == null || recipientUsers.isEmpty()) {
      return null;
    }
    return recipientUsers.stream()
        .map(RecipientUser::getId)
        .filter(Objects::nonNull)
        .toList();
  }

  public void checkSignInEmailCode(String templateCode, String email,
      String verificationCode, String codeKey, AuthenticationUser authUser) {
    // 验证邮箱验证码是否正确
    checkEmailVerificationCode(templateCode, email, verificationCode, codeKey);
    // 生成链接密钥并缓存为了后续邮件登录认证
    String linkSecret = randomAlphabetic(MAX_LINK_SECRET_LENGTH);
    authUser.setLinkSecret(linkSecret); // 属于内部认证参数，代替密码认证
    stringRedisService.set(
        String.format(CACHE_EMAIL_CHECK_SECRET_PREFIX, templateCode, authUser.getId()),
        linkSecret, GMConstant.LINK_SECRET_VALID_SECOND, TimeUnit.SECONDS);
  }

  public void checkSignInSmsCode(String templateCode, String mobile, String verificationCode,
      String codeKey, AuthenticationUser authUser) {
    // 验证短信验证码是否正确
    checkSmsVerificationCode(templateCode, mobile, verificationCode, codeKey);
    // 生成链接密钥并缓存为了后续短信登录认证
    String linkSecret = randomAlphabetic(MAX_LINK_SECRET_LENGTH);
    authUser.setLinkSecret(linkSecret); // 属于内部认证参数，代替密码认证
    stringRedisService.set(
        String.format(CACHE_SMS_CHECK_SECRET_PREFIX, templateCode, authUser.getId()),
        linkSecret, GMConstant.LINK_SECRET_VALID_SECOND, TimeUnit.SECONDS);
  }

  public void cacheEmailVerificationCode(String templateCode, String email,
      String verificationCode, int validSeconds, String codeKey) {
    // 缓存验证码key
    String cacheCodeKey = getEmailVerificationCodeCacheKey("codeKey", email);
    stringRedisService.set(cacheCodeKey, codeKey, validSeconds, TimeUnit.SECONDS);

    // 缓存邮箱验证码
    String cacheKey = getEmailVerificationCodeCacheKey(templateCode, email);
    stringRedisService.set(cacheKey, verificationCode, validSeconds, TimeUnit.SECONDS);
  }

  public void cacheSmsVerificationCode(String templateCode, String phone,
      String verificationCode, int validSeconds, String codeKey) {
    // 缓存验证码key
    String cacheCodeKey = getSmsVerificationCodeCacheKey("codeKey", phone);
    stringRedisService.set(cacheCodeKey, codeKey, validSeconds, TimeUnit.SECONDS);

    // 缓存短信验证码
    String cacheKey = getSmsVerificationCodeCacheKey(templateCode, phone);
    stringRedisService.set(cacheKey, verificationCode, validSeconds, TimeUnit.SECONDS);
  }

  public void checkEmailVerificationCode(String templateCode, String email,
      String verificationCode, String codeKey) {
    // 验证验证码key是否存在
    String cacheCodeKey = getEmailVerificationCodeCacheKey("codeKey", email);
    String cachedCode = stringRedisService.get(cacheCodeKey);
    ProtocolAssert.assertNotEmpty(cachedCode, EMAIL_VERIFY_CODE_EXPIRED);
    ProtocolAssert.assertTrue(equalsIgnoreCase(codeKey, cachedCode), EMAIL_VERIFY_CODE_ERROR);
    // 删除验证码key缓存
    deleteVerificationCodeCache(cacheCodeKey, "codeKey", email);

    // 验证邮箱验证码是否正确
    String cacheVcKey = getEmailVerificationCodeCacheKey(templateCode, email);
    String cachedVc = stringRedisService.get(cacheVcKey);
    ProtocolAssert.assertNotEmpty(cachedVc, EMAIL_VERIFY_CODE_EXPIRED);
    ProtocolAssert.assertTrue(equalsIgnoreCase(verificationCode, cachedVc),
        EMAIL_VERIFY_CODE_ERROR);
    // 删除验证码缓存
    deleteEmailVerificationCodeCache(cacheVcKey, templateCode, email);
  }

  public void checkSmsVerificationCode(String templateCode, String mobile, String code,
      String codeKey) {
    // 验证验证码key是否存在
    String cacheCodeKey = getSmsVerificationCodeCacheKey("codeKey", mobile);
    String cachedCode = stringRedisService.get(cacheCodeKey);
    assertNotEmpty(cachedCode, SMS_VERIFY_CODE_EXPIRED);
    ProtocolAssert.assertTrue(equalsIgnoreCase(codeKey, cachedCode), SMS_VERIFY_CODE_ERROR);
    // 删除验证码key缓存
    deleteVerificationCodeCache(cacheCodeKey, "codeKey", mobile);

    // 验证短信验证码是否正确
    String cacheKey = getSmsVerificationCodeCacheKey(templateCode, mobile);
    String cachedVc = stringRedisService.get(cacheKey);
    ProtocolAssert.assertNotEmpty(cachedVc, SMS_VERIFY_CODE_EXPIRED);
    ProtocolAssert.assertTrue(equalsIgnoreCase(code, cachedVc), SMS_VERIFY_CODE_ERROR);
    // 删除验证码缓存
    deleteVerificationCodeCache(cacheKey, templateCode, mobile);
  }

  private void deleteEmailVerificationCodeCache(String cacheKey, String templateCode,
      String email) {
    stringRedisService.delete(cacheKey);
    stringRedisService.delete(getEmailVerificationCodeRepeatCheckKey(templateCode, email));
  }

  private void deleteVerificationCodeCache(String cacheKey, String templateCode, String email) {
    stringRedisService.delete(cacheKey);
    stringRedisService.delete(getSmsVerificationCodeRepeatCheckKey(templateCode, email));
  }

  private List<AuthenticationUser> checkEmailUserExist(String email) {
    List<AuthenticationUser> users = authenticationUserQuery.findByEmail(email);
    assertResourceNotFound(users, EMAIL_NOT_EXIST_T, new Object[]{email});
    return users;
  }

  private List<AuthenticationUser> checkMobileUserExist(String mobile) {
    List<AuthenticationUser> users = authenticationUserQuery.findByMobile(mobile);
    assertResourceNotFound(users, MOBILE_NOT_EXIST_T, new Object[]{mobile});
    return users;
  }

  /**
   * 检查登录密码错误次数限制和安全设置
   */
  private void checkLoginPasswordErrors(String finalAccount) {
    String passwordLockedCacheKey = format(CACHE_PASSWORD_ERROR_LOCKED_PREFIX, finalAccount);
    String passwordLockedMinutes = stringRedisService.get(passwordLockedCacheKey);
    BizAssert.assertTrue(isNull(passwordLockedMinutes), LOGIN_PASSWORD_ERROR_LOCKED_RETRY_CODE,
        LOGIN_PASSWORD_ERROR_LOCKED_RETRY_T, new Object[]{passwordLockedMinutes});

    String passwordErrorNumCacheKey = format(CACHE_PASSWORD_ERROR_NUM_PREFIX, finalAccount);
    String passwordErrorNum = stringRedisService.get(passwordErrorNumCacheKey);
    if (isNull(passwordErrorNum)) {
      return;
    }

    // 检查是否启用了登录限制
    Security security = securityQuery.getLoginSecurityConfig();
    if (security.getStatus().isEnabled()) {
      LoginSecurityConfig config = (LoginSecurityConfig) security.getConfig();
      int errorCount = Integer.parseInt(passwordErrorNum);
      if (errorCount >= config.getMaxLoginAttempts()) {
        stringRedisService.set(passwordLockedCacheKey, passwordErrorNum,
            config.getAccountLockoutDurationMinutes(), TimeUnit.MINUTES);
        stringRedisService.delete(passwordErrorNumCacheKey);

        throw BizException.of(LOGIN_PASSWORD_ERROR_OVER_LIMIT_CODE,
            LOGIN_PASSWORD_ERROR_OVER_LIMIT_T, new Object[]{passwordErrorNum});
      }
    }
  }

  /**
   * 记录登录密码错误尝试次数，用于安全追踪
   */
  public int recordLoginInPasswordErrors(String innerAccount, Security security) {
    String passwordLockedCacheKey = format(CACHE_PASSWORD_ERROR_LOCKED_PREFIX, innerAccount);
    String passwordLockedMinutes = stringRedisService.get(passwordLockedCacheKey);
    if (Objects.nonNull(passwordLockedMinutes)) {
      // 账户锁定后不再记录错误
      return 0;
    }
    String passwordErrorNumCacheKey = format(CACHE_PASSWORD_ERROR_NUM_PREFIX, innerAccount);
    String passwordErrorNum = stringRedisService.get(passwordErrorNumCacheKey);
    if (Objects.nonNull(passwordErrorNum)) {
      passwordErrorNum = String.valueOf(parseInt(passwordErrorNum) + 1);
    } else {
      passwordErrorNum = String.valueOf(1);
    }

    LoginSecurityConfig config = (LoginSecurityConfig) security.getConfig();
    stringRedisService.set(passwordErrorNumCacheKey, passwordErrorNum,
        config.getAccountLockoutDurationMinutes(), TimeUnit.MINUTES);

    // 检查是否启用了登录限制，如果需要则锁定账户
    if (security.getStatus().isEnabled()) {
      if (Integer.parseInt(passwordErrorNum) >= config.getMaxLoginAttempts()) {
        stringRedisService.set(passwordLockedCacheKey, passwordErrorNum,
            config.getAccountLockoutDurationMinutes(), TimeUnit.MINUTES);
        stringRedisService.delete(passwordErrorNumCacheKey);
      }
    }
    return Integer.parseInt(passwordErrorNum);
  }

  /**
   * 记录登录设备和登录历史日志
   */
  private void recordLoginDeviceAndHistory(Long userId, String username, SignInType loginType,
      SuccessStatus loginStatus, String failureReason) {
    try {
      Principal principal = PrincipalContext.get();
      String deviceId = principal.getDeviceInfo().getDeviceId();
      LocalDateTime now = LocalDateTime.now();

      LoginHistory loginHistory = toLoginHistory(userId, username,
          loginType, loginStatus, failureReason, now, principal);
      loginHistoryCmd.create(loginHistory);

      // 记录登录设备（仅登录成功时）
      if (SuccessStatus.SUCCESS.equals(loginStatus)) {
        // 先将该用户的其他设备的isCurrent设置为false（使用SQL批量更新）
        loginDeviceCmd.updateOtherDevicesIsCurrentToFalse(userId, deviceId);

        // 查找现有设备记录
        LoginDevice device = loginDeviceQuery.findByUserIdAndDeviceId(userId, deviceId);
        device = toLoginDevice(userId, device, principal, now);
        // 保存当前设备
        loginDeviceCmd.create(device);
      }
    } catch (Exception e) {
      log.error("记录登录设备和登录历史日志失败: " + e.getMessage(), e);
    }
  }

  /**
   * 生成唯一的OAuth用户名
   */
  private String generateOAuthUsername(OAuthProvider provider, String openId) {
    String providerCode = provider.name().toLowerCase();
    String username = providerCode + "_" + openId;
    // 如果用户名已存在，添加随机后缀
    int suffix = 1;
    while (authenticationUserRepo.findByUsername(username) != null) {
      username = providerCode + "_" + openId + "_" + suffix++;
    }
    return username;
  }

  /**
   * 获取第三方登录提供商的显示名称
   */
  private String getProviderDisplayName(OAuthProvider provider) {
    if (provider == null) {
      return "未知";
    }
    return switch (provider) {
      case WECHAT -> "微信";
      case GITHUB -> "GitHub";
      case GOOGLE -> "Google";
    };
  }

  @Override
  protected BaseRepository<AuthenticationUser, Long> getRepository() {
    return authenticationUserRepo;
  }
}
