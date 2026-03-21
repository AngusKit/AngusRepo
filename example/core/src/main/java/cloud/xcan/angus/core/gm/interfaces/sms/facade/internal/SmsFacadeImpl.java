package cloud.xcan.angus.core.gm.interfaces.sms.facade.internal;

import static cloud.xcan.angus.api.commonlink.GMConstant.DEFAULT_SMS_LANGUAGE;
import static cloud.xcan.angus.core.jpa.criteria.SearchCriteriaBuilder.getMatchSearchFields;
import static cloud.xcan.angus.core.utils.CoreUtils.buildVoPageResult;

import cloud.xcan.angus.api.commonlink.Language;
import cloud.xcan.angus.api.gm.sms.dto.SmsSendBatchDto;
import cloud.xcan.angus.api.gm.sms.dto.SmsSendDto;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendBatchVo;
import cloud.xcan.angus.api.gm.sms.vo.SmsSendVo;
import cloud.xcan.angus.core.biz.NameJoin;
import cloud.xcan.angus.core.gm.application.cmd.sms.SmsCmd;
import cloud.xcan.angus.core.gm.application.query.sms.SmsQuery;
import cloud.xcan.angus.core.gm.domain.sms.Sms;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.SmsFacade;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsRecordFindDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.dto.SmsTestDto;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.internal.assembler.SmsAssembler;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsRecordVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsStatsVo;
import cloud.xcan.angus.core.gm.interfaces.sms.facade.vo.SmsTestVo;
import cloud.xcan.angus.core.jpa.criteria.GenericSpecification;
import cloud.xcan.angus.remote.PageResult;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public class SmsFacadeImpl implements SmsFacade {

  @Resource
  private SmsCmd smsCmd;

  @Resource
  private SmsQuery smsQuery;

  @Override
  public SmsSendVo send(SmsSendDto dto) {
    Language language = dto.getLanguage() != null
        ? dto.getLanguage() : Language.valueOf(DEFAULT_SMS_LANGUAGE);
    Sms sms = smsCmd.send(dto.getTemplateCode(), language, dto.getPhone(), dto.getParams());
    return SmsAssembler.toSmsSendVo(sms);
  }

  @Override
  public SmsSendBatchVo sendBatch(SmsSendBatchDto dto) {
    Language language = dto.getLanguage() != null
        ? dto.getLanguage() : Language.valueOf(DEFAULT_SMS_LANGUAGE);
    List<Sms> resultList = smsCmd.sendBatch(dto.getTemplateCode(), language, dto.getPhones(),
        dto.getParams());
    return SmsAssembler.toSmsSendBatchVo(resultList);
  }

  @Override
  public SmsTestVo test(SmsTestDto dto) {
    Sms sms = smsCmd.test(dto.getPhone(), dto.getContent());
    return SmsAssembler.toSmsTestVo(sms);
  }

  @Override
  public SmsStatsVo getStats() {
    return smsQuery.getStats();
  }

  @NameJoin
  @Override
  public PageResult<SmsRecordVo> listRecords(SmsRecordFindDto dto) {
    GenericSpecification<Sms> spec = SmsAssembler.getSpecification(dto);
    Page<Sms> page = smsQuery.findRecords(spec, dto.tranPage(),
        dto.fullTextSearch, getMatchSearchFields(dto.getClass()));
    return buildVoPageResult(page, SmsAssembler::toRecordVo);
  }

}
