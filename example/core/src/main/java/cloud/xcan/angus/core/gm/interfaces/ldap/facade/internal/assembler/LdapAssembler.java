package cloud.xcan.angus.core.gm.interfaces.ldap.facade.internal.assembler;

import static cloud.xcan.angus.spec.utils.ObjectUtils.nullSafe;

import cloud.xcan.angus.core.gm.domain.ldap.Ldap;
import cloud.xcan.angus.core.gm.domain.ldap.LdapSyncHistory;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapStatus;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapSyncStatus;
import cloud.xcan.angus.core.gm.domain.ldap.enums.LdapSyncType;
import cloud.xcan.angus.core.gm.infra.ldap.LdapClientService;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConfigCreateDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapConfigUpdateDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.dto.LdapSyncHistoryFindDto;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapAuthTestVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapConfigVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapConnectionTestVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapGroupMembersVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapGroupVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapSyncDetailVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapSyncHistoryVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapSyncResultVo;
import cloud.xcan.angus.core.gm.interfaces.ldap.facade.vo.LdapUserVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder;
import cloud.xcan.angus.remote.search.SearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class LdapAssembler {

  public static Ldap toCreateDomain(LdapConfigCreateDto dto) {
    Ldap ldap = new Ldap();
    ldap.setName(dto.getName());
    ldap.setType(dto.getType());
    ldap.setServerUrl(dto.getServer());
    ldap.setBaseDn(dto.getBaseDN());
    ldap.setBindDn(dto.getBindDN());
    if (dto.getBindPassword() != null && !dto.getBindPassword().isEmpty()) {
      ldap.setBindPassword(dto.getBindPassword());
    }
    ldap.setUserFilter(dto.getUserSearchFilter());
    ldap.setGroupFilter(dto.getGroupSearchFilter());
    ldap.setSyncEnabled(nullSafe(dto.getSyncEnabled(), false));
    ldap.setEnabled(nullSafe(dto.getIsEnabled(), true));
    ldap.setDescription(dto.getDescription());
    if (dto.getFieldMapping() != null && !dto.getFieldMapping().isEmpty()) {
      ldap.setFieldMapping(dto.getFieldMapping());
    }
    // 设置默认状态为已断开（需要测试连接后才能设置为已连接）
    ldap.setStatus(LdapStatus.DISCONNECTED);
    return ldap;
  }

  public static Ldap toUpdateConfigDomain(LdapConfigUpdateDto dto) {
    Ldap ldap = new Ldap();
    ldap.setServerUrl(dto.getServer());
    ldap.setBaseDn(dto.getBaseDN());
    ldap.setBindDn(dto.getBindDN());
    if (dto.getBindPassword() != null && !dto.getBindPassword().isEmpty()) {
      ldap.setBindPassword(dto.getBindPassword());
    }
    ldap.setUserFilter(dto.getUserSearchFilter());
    ldap.setGroupFilter(dto.getGroupSearchFilter());
    ldap.setSyncEnabled(dto.getSyncEnabled() != null ? dto.getSyncEnabled() : false);
    if (dto.getFieldMapping() != null) {
      ldap.setFieldMapping(dto.getFieldMapping());
    }
    // 启用状态使用 updateStatus 接口单独更新
    return ldap;
  }

  public static LdapConfigVo toConfigVo(Ldap ldap) {
    if (ldap == null) {
      return null;
    }
    LdapConfigVo vo = new LdapConfigVo();
    Long id = ldap.identity();
    vo.setId(id != null ? id.toString() : null);
    vo.setName(ldap.getName());
    vo.setType(ldap.getType());
    vo.setIsEnabled(ldap.getEnabled());
    vo.setServer(ldap.getServerUrl());
    vo.setBaseDN(ldap.getBaseDn());
    vo.setBindDN(ldap.getBindDn());
    // 密码脱敏显示
    vo.setBindPassword(ldap.getBindPassword() != null ? "******" : null);
    vo.setUserSearchFilter(ldap.getUserFilter());
    vo.setGroupSearchFilter(ldap.getGroupFilter());
    vo.setSyncEnabled(ldap.getSyncEnabled());
    vo.setDescription(ldap.getDescription());
    vo.setFieldMapping(ldap.getFieldMapping());
    // 设置审计信息
    vo.setCreatedDate(ldap.getCreatedDate());
    vo.setModifiedDate(ldap.getModifiedDate());
    return vo;
  }

  public static LdapConnectionTestVo toConnectionTestVo(
      LdapClientService.LdapConnectionResult result) {
    LdapConnectionTestVo vo = new LdapConnectionTestVo();
    vo.setTestTime(java.time.LocalDateTime.now());
    vo.setConnected(result.isConnected());
    vo.setResponseTime(
        result.getResponseTime() != null ? result.getResponseTime().intValue() : null);
    if (!result.isConnected()) {
      vo.setErrorMessage(result.getErrorMessage());
    }
    if (result.isConnected() && result.getVersion() != null) {
      LdapConnectionTestVo.ServerInfo serverInfo = new LdapConnectionTestVo.ServerInfo();
      serverInfo.setVersion(result.getVersion());
      serverInfo.setVendor(result.getVendor());
      vo.setServerInfo(serverInfo);
    }
    return vo;
  }

  public static LdapAuthTestVo toAuthTestVo(LdapClientService.LdapAuthResult result) {
    LdapAuthTestVo vo = new LdapAuthTestVo();
    vo.setAuthenticated(result.isAuthenticated());
    vo.setUserDN(result.getUserDN());
    vo.setAttributes(result.getAttributes());
    return vo;
  }

  public static LdapUserVo toUserVo(LdapClientService.LdapEntry entry,
      Map<String, String> fieldMapping) {
    LdapUserVo vo = new LdapUserVo();
    vo.setDn(entry.getDn());

    if (fieldMapping != null) {
      String uidField = fieldMapping.getOrDefault("uid", "uid");
      String cnField = fieldMapping.getOrDefault("cn", "cn");
      String mailField = fieldMapping.getOrDefault("mail", "mail");
      String departmentField = fieldMapping.getOrDefault("department", "department");
      String titleField = fieldMapping.getOrDefault("title", "title");
      String mobileField = fieldMapping.getOrDefault("mobile", "mobile");

      vo.setUid(entry.getAttributeValue(uidField));
      vo.setCn(entry.getAttributeValue(cnField));
      vo.setMail(entry.getAttributeValue(mailField));
      vo.setDepartment(entry.getAttributeValue(departmentField));
      vo.setTitle(entry.getAttributeValue(titleField));
      vo.setMobile(entry.getAttributeValue(mobileField));
    } else {
      vo.setUid(entry.getAttributeValue("uid"));
      vo.setCn(entry.getAttributeValue("cn"));
      vo.setMail(entry.getAttributeValue("mail"));
      vo.setDepartment(entry.getAttributeValue("department"));
      vo.setTitle(entry.getAttributeValue("title"));
      vo.setMobile(entry.getAttributeValue("mobile"));
    }

    return vo;
  }

  public static LdapGroupVo toGroupVo(LdapClientService.LdapEntry entry) {
    LdapGroupVo vo = new LdapGroupVo();
    vo.setDn(entry.getDn());
    vo.setCn(entry.getAttributeValue("cn"));
    vo.setDescription(entry.getAttributeValue("description"));

    List<String> members = entry.getAttributeValues("member");
    vo.setMemberCount(members != null ? members.size() : 0);
    return vo;
  }

  public static LdapGroupMembersVo toGroupMembersVo(LdapClientService.LdapEntry group,
      List<LdapClientService.LdapEntry> members, Map<String, String> fieldMapping) {
    LdapGroupMembersVo vo = new LdapGroupMembersVo();
    vo.setGroupDN(group.getDn());
    vo.setGroupName(group.getAttributeValue("cn"));

    List<LdapGroupMembersVo.Member> memberList = members.stream()
        .map(member -> {
          LdapGroupMembersVo.Member m = new LdapGroupMembersVo.Member();
          m.setDn(member.getDn());
          if (fieldMapping != null) {
            String uidField = fieldMapping.getOrDefault("uid", "uid");
            String cnField = fieldMapping.getOrDefault("cn", "cn");
            String mailField = fieldMapping.getOrDefault("mail", "mail");
            m.setUid(member.getAttributeValue(uidField));
            m.setCn(member.getAttributeValue(cnField));
            m.setMail(member.getAttributeValue(mailField));
          } else {
            m.setUid(member.getAttributeValue("uid"));
            m.setCn(member.getAttributeValue("cn"));
            m.setMail(member.getAttributeValue("mail"));
          }
          return m;
        })
        .collect(Collectors.toList());

    vo.setMembers(memberList);
    return vo;
  }

  public static LdapSyncHistoryVo toSyncHistoryVo(LdapSyncHistory history) {
    LdapSyncHistoryVo vo = new LdapSyncHistoryVo();
    vo.setId(history.getId());
    vo.setStartTime(history.getStartTime());
    vo.setEndTime(history.getEndTime());
    vo.setDuration(history.getDuration());
    vo.setStatus(history.getStatus());
    vo.setTotalUsers(history.getTotalUsers());
    vo.setNewUsers(history.getNewUsers());
    vo.setUpdatedUsers(history.getUpdatedUsers());
    vo.setDeletedUsers(history.getDeletedUsers());
    vo.setFailedUsers(history.getFailedUsers());
    vo.setSyncType(history.getSyncType());
    vo.setCreatedDate(history.getCreatedDate());
    return vo;
  }

  public static LdapSyncHistory toLdapSyncHistory(Ldap config) {
    LdapSyncHistory history = new LdapSyncHistory();
    history.setLdapId(config.getId());
    history.setStatus(LdapSyncStatus.RUNNING);
    history.setSyncType(LdapSyncType.MANUAL);
    history.setStartTime(java.time.LocalDateTime.now());
    history.setTotalUsers(0);
    history.setNewUsers(0);
    history.setUpdatedUsers(0);
    history.setDeletedUsers(0);
    history.setFailedUsers(0);
    return history;
  }

  public static LdapSyncResultVo toSyncResultVo(LdapSyncHistory history) {
    LdapSyncResultVo vo = new LdapSyncResultVo();
    vo.setId(history.getId().toString());
    vo.setStartTime(history.getStartTime());
    vo.setEndTime(history.getEndTime());
    vo.setStatus(history.getStatus());
    vo.setTotalUsers(history.getTotalUsers());
    vo.setNewUsers(history.getNewUsers());
    vo.setUpdatedUsers(history.getUpdatedUsers());
    vo.setDeletedUsers(history.getDeletedUsers());
    vo.setFailedUsers(history.getFailedUsers());
    return vo;
  }

  public static LdapSyncDetailVo toSyncDetailVo(LdapSyncHistory history) {
    LdapSyncDetailVo vo = new LdapSyncDetailVo();
    vo.setId(history.getId().toString());
    vo.setStartTime(history.getStartTime());
    vo.setEndTime(history.getEndTime());
    vo.setDuration(history.getDuration());
    vo.setStatus(history.getStatus());
    vo.setTotalUsers(history.getTotalUsers());
    vo.setNewUsers(history.getNewUsers());
    vo.setUpdatedUsers(history.getUpdatedUsers());
    vo.setDeletedUsers(history.getDeletedUsers());
    vo.setFailedUsers(history.getFailedUsers());
    vo.setSyncType(
        history.getSyncType() != null ? history.getSyncType().name().toLowerCase() : null);
    return vo;
  }

  public static GenericSpecification<LdapSyncHistory> getSyncHistorySpecification(
      LdapSyncHistoryFindDto dto) {
    Set<SearchCriteria> filters = new SearchCriteriaBuilder<>(dto)
        .rangeSearchFields("id", "createdDate", "modifiedDate", "startTime", "endTime")
        .orderByFields("id", "createdDate", "modifiedDate", "startTime", "endTime")
        .build();
    return new GenericSpecification<>(filters);
  }

  /**
   * 构建用户搜索过滤器
   */
  public static String buildUserSearchFilter(String keyword, String baseFilter) {
    if (keyword == null || keyword.isEmpty()) {
      return baseFilter != null ? baseFilter : "(objectClass=person)";
    }

    String keywordFilter = String.format("(|(cn=*%s*)(uid=*%s*)(mail=*%s*))", keyword, keyword,
        keyword);

    if (baseFilter != null && !baseFilter.isEmpty()) {
      return String.format("(&%s%s)", baseFilter, keywordFilter);
    }

    return String.format("(&(objectClass=person)%s)", keywordFilter);
  }
}
