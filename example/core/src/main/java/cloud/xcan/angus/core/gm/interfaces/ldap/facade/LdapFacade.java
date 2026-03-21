package cloud.xcan.angus.core.gm.interfaces.ldap.facade;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapAuthTestDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConfigCreateDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConnectionTestDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapSyncHistoryFindDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapUserSearchDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapAuthTestVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapConfigVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapConnectionTestVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapGroupMembersVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapGroupVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapSyncAllResultVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapSyncDetailVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapSyncHistoryVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapSyncResultVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapUserVo;
import cloud.xcan.angus.remote.PageResult;
import java.util.List;

public interface LdapFacade {

  // ==================== LDAP配置 ====================

  /**
   * 创建LDAP配置
   */
  LdapConfigVo create(LdapConfigCreateDto dto);

  /**
   * 删除 LDAP配置
   */
  void delete(Long id);

  /**
   * 获取所有LDAP配置
   */
  List<LdapConfigVo> listConfigs();

  /**
   * 更新LDAP配置
   */
  LdapConfigVo updateConfig(Long id, LdapConfigUpdateDto dto);

  /**
   * 更新LDAP配置启用状态（允许同时启用多个LDAP）
   */
  LdapConfigVo updateStatus(Long id, EnabledStatusUpdateDto dto);

  // ==================== 连接测试 ====================

  /**
   * 测试LDAP连接
   */
  LdapConnectionTestVo testConnection(LdapConnectionTestDto dto);

  /**
   * 测试LDAP认证
   */
  LdapAuthTestVo testAuth(Long id, LdapAuthTestDto dto);

  // ==================== 用户同步 ====================

  /**
   * 手动同步指定LDAP配置的用户
   */
  LdapSyncResultVo syncUsers(Long configId);

  /**
   * 同步所有已启用的LDAP配置的用户
   */
  LdapSyncAllResultVo syncAllUsers();

  /**
   * 获取同步历史记录
   */
  PageResult<LdapSyncHistoryVo> listSyncHistory(LdapSyncHistoryFindDto dto);

  /**
   * 获取同步详情
   */
  LdapSyncDetailVo getSyncDetail(Long id);

  // ==================== LDAP用户搜索 ====================

  /**
   * 搜索LDAP用户
   */
  List<LdapUserVo> searchUsers(LdapUserSearchDto dto);

  // ==================== LDAP组管理 ====================

  /**
   * 获取LDAP组列表
   */
  List<LdapGroupVo> listGroups();

  /**
   * 获取LDAP组成员
   */
  LdapGroupMembersVo getGroupMembers(String groupDN);

}
