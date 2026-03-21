package cloud.xcan.angus.core.gm.interfaces.ldap.facade.internal;

import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.gm.EnabledStatusUpdateDto;
import cloud.xcan.angus.core.gm.application.cmd.ldap.LdapCmd;
import cloud.xcan.angus.core.gm.application.query.ldap.LdapQuery;
import cloud.xcan.angus.core.gm.application.query.ldap.LdapSyncHistoryQuery;
import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistory;
import cloud.xcan.angus.core.gm.infra.ldap.LdapClientService;
import cloud.xcan.angus.core.gm.infra.ldap.LdapClientServiceFactory;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.LdapFacade;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapAuthTestDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConfigCreateDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConnectionTestDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapSyncHistoryFindDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapUserSearchDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.internal.assembler.LdapAssembler;
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
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import cloud.xcan.angus.remote.message.ProtocolException;
import cloud.xcan.angus.remote.message.http.ResourceNotFound;
import jakarta.annotation.Resource;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

/**
 * LDAP门面服务实现
 */
@Component
public class LdapFacadeImpl implements LdapFacade {

  @Resource
  private LdapCmd ldapCmd;

  @Resource
  private LdapQuery ldapQuery;

  @Resource
  private LdapSyncHistoryQuery ldapSyncHistoryQuery;

  @Resource
  private LdapClientServiceFactory ldapClientServiceFactory;

  @Override
  public LdapConfigVo create(LdapConfigCreateDto dto) {
    Ldap ldap = LdapAssembler.toCreateDomain(dto);
    Ldap saved = ldapCmd.create(ldap);
    return LdapAssembler.toConfigVo(saved);
  }

  @Override
  public void delete(Long id) {
    ldapCmd.delete(id);
  }

  @Override
  public List<LdapConfigVo> listConfigs() {
    List<Ldap> ldaps = ldapQuery.findAll();
    return ldaps.stream()
        .map(LdapAssembler::toConfigVo)
        .collect(Collectors.toList());
  }

  @Override
  public LdapConfigVo updateConfig(Long id, LdapConfigUpdateDto dto) {
    Ldap ldap = LdapAssembler.toUpdateConfigDomain(dto);
    Ldap saved = ldapCmd.updateConfig(id, ldap);
    return LdapAssembler.toConfigVo(saved);
  }

  @Override
  public LdapConfigVo updateStatus(Long id, EnabledStatusUpdateDto dto) {
    Ldap saved = ldapCmd.updateStatus(id, dto.getStatus());
    return LdapAssembler.toConfigVo(saved);
  }

  @Override
  public LdapConnectionTestVo testConnection(LdapConnectionTestDto dto) {
    LdapClientService clientService = ldapClientServiceFactory.create(dto);
    LdapClientService.LdapConnectionResult result = clientService.testConnection();
    return LdapAssembler.toConnectionTestVo(result);
  }

  @Override
  public LdapAuthTestVo testAuth(Long id, LdapAuthTestDto dto) {
    Ldap config = ldapQuery.findAndCheck(id);
    if (!config.getEnabled()) {
      throw ProtocolException.of("LDAP配置未启用");
    }
    LdapClientService clientService = ldapClientServiceFactory.create(config);
    LdapClientService.LdapAuthResult result = clientService.authenticate(
        dto.getUsername(), dto.getPassword());
    return LdapAssembler.toAuthTestVo(result);
  }

  @Override
  public LdapSyncResultVo syncUsers(Long configId) {
    // 获取LDAP配置：指定ID或第一个启用的配置
    Ldap config = configId != null ? ldapQuery.findAndCheck(configId)
        : ldapQuery.getCurrentConfig();
    if (config == null) {
      throw ProtocolException.of("LDAP配置不存在，请先配置LDAP服务器");
    }
    if (!config.getEnabled()) {
      throw ProtocolException.of("LDAP配置未启用");
    }
    // 创建同步历史记录
    LdapSyncHistory history = LdapAssembler.toLdapSyncHistory(config);
    // 执行同步（非测试模式）
    LdapSyncHistory saved = ldapCmd.syncUsers(config, history, false);
    // 转换为VO
    return LdapAssembler.toSyncResultVo(saved);
  }

  @Override
  public LdapSyncAllResultVo syncAllUsers() {
    List<Ldap> enabledConfigs = ldapQuery.findByEnabled(true);
    if (enabledConfigs == null || enabledConfigs.isEmpty()) {
      throw ProtocolException.of("没有已启用的LDAP配置");
    }
    List<LdapSyncAllResultVo.LdapSyncAllResultItemVo> results = new java.util.ArrayList<>();
    for (Ldap config : enabledConfigs) {
      LdapSyncHistory history = LdapAssembler.toLdapSyncHistory(config);
      LdapSyncHistory saved = ldapCmd.syncUsers(config, history, false);
      LdapSyncAllResultVo.LdapSyncAllResultItemVo item = new LdapSyncAllResultVo.LdapSyncAllResultItemVo();
      item.setConfigId(config.getId());
      item.setConfigName(config.getName());
      item.setResult(LdapAssembler.toSyncResultVo(saved));
      results.add(item);
    }
    LdapSyncAllResultVo vo = new LdapSyncAllResultVo();
    vo.setResults(results);
    return vo;
  }

  @Override
  public PageResult<LdapSyncHistoryVo> listSyncHistory(LdapSyncHistoryFindDto dto) {
    GenericSpecification<LdapSyncHistory> spec = LdapAssembler.getSyncHistorySpecification(dto);
    Page<LdapSyncHistory> page = ldapSyncHistoryQuery.find(spec, dto.tranPage());
    return buildVoPageResult(page, LdapAssembler::toSyncHistoryVo);
  }

  @Override
  public LdapSyncDetailVo getSyncDetail(Long id) {
    LdapSyncHistory history = ldapSyncHistoryQuery.findAndCheck(id);
    return LdapAssembler.toSyncDetailVo(history);
  }

  @Override
  public List<LdapUserVo> searchUsers(LdapUserSearchDto dto) {
    List<Ldap> enabledConfigs = ldapQuery.findByEnabled(true);
    if (enabledConfigs == null || enabledConfigs.isEmpty()) {
      throw ProtocolException.of("没有已启用的LDAP配置");
    }
    Integer limit = dto.getLimit() != null ? dto.getLimit() : 50;
    int limitPerConfig = Math.max(limit / enabledConfigs.size(), 10);
    Set<String> seenDns = new LinkedHashSet<>();
    List<LdapUserVo> result = new ArrayList<>();
    for (Ldap config : enabledConfigs) {
      try {
        LdapClientService clientService = ldapClientServiceFactory.create(config);
        String filter = LdapAssembler.buildUserSearchFilter(dto.getKeyword(),
            config.getUserFilter());
        String baseDN = dto.getSearchBase() != null ? dto.getSearchBase() : config.getBaseDn();
        String[] attributes = new String[]{"uid", "cn", "mail", "department", "title", "mobile"};
        List<LdapClientService.LdapEntry> entries
            = clientService.searchUsers(baseDN, filter, attributes, limitPerConfig);
        Map<String, String> fieldMapping = ldapQuery.getFieldMapping(config.getId());
        for (LdapClientService.LdapEntry entry : entries) {
          if (seenDns.add(entry.getDn())) {
            result.add(LdapAssembler.toUserVo(entry, fieldMapping));
            if (result.size() >= limit) {
              return result;
            }
          }
        }
      } catch (Exception e) {
        // 单个配置查询失败时跳过，继续查询其他配置
      }
    }
    return result;
  }

  @Override
  public List<LdapGroupVo> listGroups() {
    List<Ldap> enabledConfigs = ldapQuery.findByEnabled(true);
    if (enabledConfigs == null || enabledConfigs.isEmpty()) {
      throw ProtocolException.of("没有已启用的LDAP配置");
    }
    Set<String> seenDns = new LinkedHashSet<>();
    List<LdapGroupVo> result = new ArrayList<>();
    for (Ldap config : enabledConfigs) {
      try {
        LdapClientService clientService = ldapClientServiceFactory.create(config);
        String baseDN = config.getBaseDn();
        String filter = config.getGroupFilter() != null
            ? config.getGroupFilter() : "(objectClass=group)";
        String[] attributes = new String[]{"cn", "description", "member"};
        List<LdapClientService.LdapEntry> entries
            = clientService.searchGroups(baseDN, filter, attributes);
        for (LdapClientService.LdapEntry entry : entries) {
          if (seenDns.add(entry.getDn())) {
            result.add(LdapAssembler.toGroupVo(entry));
          }
        }
      } catch (Exception e) {
        // 单个配置查询失败时跳过，继续查询其他配置
      }
    }
    return result;
  }

  @Override
  public LdapGroupMembersVo getGroupMembers(String groupDN) {
    if (groupDN == null || groupDN.isEmpty()) {
      throw ProtocolException.of("组DN不能为空");
    }
    String decodedGroupDN;
    try {
      decodedGroupDN = URLDecoder.decode(groupDN, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw ProtocolException.of("无效的组DN: {0}", new Object[]{groupDN});
    }
    List<Ldap> enabledConfigs = ldapQuery.findByEnabled(true);
    if (enabledConfigs == null || enabledConfigs.isEmpty()) {
      throw ProtocolException.of("没有已启用的LDAP配置");
    }
    for (Ldap config : enabledConfigs) {
      try {
        LdapClientService clientService = ldapClientServiceFactory.create(config);
        String[] groupAttributes = new String[]{"cn", "description", "member"};
        LdapClientService.LdapEntry group = clientService.getEntry(decodedGroupDN, groupAttributes);
        if (group != null) {
          String[] memberAttributes = new String[]{"uid", "cn", "mail"};
          List<LdapClientService.LdapEntry> members = clientService.getGroupMembers(decodedGroupDN,
              memberAttributes);
          Map<String, String> fieldMapping = ldapQuery.getFieldMapping(config.getId());
          return LdapAssembler.toGroupMembersVo(group, members, fieldMapping);
        }
      } catch (Exception e) {
        // 当前配置未找到该组，尝试下一个配置
      }
    }
    throw ResourceNotFound.of("LDAP组「{0}」不存在", new Object[]{decodedGroupDN});
  }

}
