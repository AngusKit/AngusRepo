package cloud.xcan.angus.core.gm.interfaces.authentication;

import cloud.xcan.angus.api.commonlink.user.UserInfo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.AuthenticationFacade;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.EmailCodeSendDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.InviteCodeVerifyDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.PasswordResetDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.RefreshTokenDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.SmsCodeSendDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserLogoutDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignInDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.dto.UserSignupDto;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.CaptchaVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.InviteCodeVerifyVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.UserSignInVo;
import cloud.xcan.angus.core.gm.interfaces.authentication.facade.vo.VerificationCodeSendVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.LoginSecurityConfigVo;
import cloud.xcan.angus.core.gm.interfaces.security.facade.vo.PasswordPolicyVo;
import cloud.xcan.angus.api.gm.user.vo.UserDetailVo;
import cloud.xcan.angus.remote.ApiLocaleResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "认证授权 - 用户登录、注册、密码找回、第三方登录")
@Validated
@RestController
@RequestMapping("/pubapi/v1/auth/user")
public class AuthenticationPubRest {

  @Resource
  private AuthenticationFacade authenticationFacade;

  @Operation(operationId = "signIn", summary = "用户登录", description = "支持账号密码、短信验证码、邮箱验证码三种登录方式")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "登录成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/signin")
  public ApiLocaleResult<UserSignInVo> signIn(
      @Valid @RequestBody UserSignInDto dto) {
    return ApiLocaleResult.success(authenticationFacade.signIn(dto));
  }

  //  @Operation(operationId = "socialSignIn", summary = "OAuth第三方登录", description = "支持微信、钉钉、GitHub等第三方登录")
  //  @ApiResponses(value = {
  //      @ApiResponse(responseCode = "200", description = "登录成功")
  //  })
  //  @ResponseStatus(HttpStatus.OK)
  //  @PostMapping("/social-signin")
  //  public ApiLocaleResult<UserSignInVo> socialSignIn(
  //      @Valid @RequestBody SocialSignInDto dto) {
  //    return ApiLocaleResult.success(authenticationFacade.socialSignIn(dto));
  //  }

  @Operation(operationId = "signUp", summary = "用户注册", description = "支持短信验证码和邮箱验证码两种注册方式")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "注册成功")
  })
  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("/signup")
  public ApiLocaleResult<UserDetailVo> signUp(
      @Valid @RequestBody UserSignupDto dto) {
    return ApiLocaleResult.success(authenticationFacade.signUp(dto));
  }

  @Operation(operationId = "resetPassword", summary = "重置密码", description = "使用验证码重置用户密码")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "密码重置成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/password/reset")
  public ApiLocaleResult<?> resetPassword(
      @Valid @RequestBody PasswordResetDto dto) {
    authenticationFacade.resetPassword(dto);
    return ApiLocaleResult.success();
  }

  @Operation(operationId = "refreshToken", summary = "刷新Token", description = "使用refreshToken刷新accessToken")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Token刷新成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/refresh")
  public ApiLocaleResult<UserSignInVo> refreshToken(
      @Valid @RequestBody RefreshTokenDto dto) {
    return ApiLocaleResult.success(authenticationFacade.refreshToken(dto));
  }

  @Operation(operationId = "logout", summary = "退出登录", description = "退出当前登录，使token失效")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "退出登录成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/logout")
  public ApiLocaleResult<?> logout(
      @Parameter(description = "访问令牌（认证头参数）", required = false) @RequestHeader(value = "Authorization", required = false) String authorization,
      @RequestBody UserLogoutDto dto) {
    String finalAccessToken = authorization != null
        ? authorization.replace("Bearer ", "") : dto.getAccessToken();
    authenticationFacade.logout(finalAccessToken);
    return ApiLocaleResult.success(null);
  }

  @Operation(operationId = "findUsersByAccount", summary = "根据账号查询用户信息",
      description = "根据用户账号（手机号或邮箱）查询用户多账号信息，用于相同手机号或邮箱注册在多租户账号下时，选择具体登录用户")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "查询成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/users")
  public ApiLocaleResult<List<UserInfo>> findUsersByAccount(
      @Parameter(description = "用户账号（手机号或邮箱）", required = true) @RequestParam String account) {
    return ApiLocaleResult.success(authenticationFacade.findUsersByAccount(account));
  }

  @Operation(operationId = "getUserPasswordPolicy", summary = "获取密码策略配置", description = "获取密码策略配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/password-policy")
  public ApiLocaleResult<PasswordPolicyVo> getPasswordPolicy() {
    return ApiLocaleResult.success(authenticationFacade.getPasswordPolicy());
  }

  @Operation(operationId = "getUserLoginSecurityConfig", summary = "获取登录安全配置", description = "获取登录安全配置")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/login-security")
  public ApiLocaleResult<LoginSecurityConfigVo> getLoginSecurityConfig() {
    return ApiLocaleResult.success(authenticationFacade.getLoginSecurityConfig());
  }

  @Operation(operationId = "getCaptcha", summary = "获取验证码图片", description = "获取图形验证码图片，用于账号密码登录")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "验证码图片获取成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/captcha")
  public ApiLocaleResult<CaptchaVo> getCaptcha() {
    return ApiLocaleResult.success(authenticationFacade.getCaptcha());
  }

  @Operation(operationId = "verifyInviteCode", summary = "验证邀请码", description = "验证邀请码是否有效，返回租户名称、邀请人等信息")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "验证成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/invite-code/verify")
  public ApiLocaleResult<InviteCodeVerifyVo> verifyInviteCode(
      @Valid @RequestBody InviteCodeVerifyDto dto) {
    return ApiLocaleResult.success(authenticationFacade.verifyInviteCode(dto));
  }

  @Operation(operationId = "sendSmsCode", summary = "发送短信验证码", description = "发送短信验证码，支持登录、注册、找回密码等场景")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "验证码发送成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/sms/send-code")
  public ApiLocaleResult<VerificationCodeSendVo> sendSmsCode(
      @Valid @RequestBody SmsCodeSendDto dto) {
    return ApiLocaleResult.success(authenticationFacade.sendSmsCode(dto));
  }

  @Operation(operationId = "sendEmailCode", summary = "发送邮箱验证码", description = "发送邮箱验证码，支持登录、注册、找回密码等场景")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "验证码发送成功")
  })
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/email/send-code")
  public ApiLocaleResult<VerificationCodeSendVo> sendEmailCode(
      @Valid @RequestBody EmailCodeSendDto dto) {
    return ApiLocaleResult.success(authenticationFacade.sendEmailCode(dto));
  }

}
