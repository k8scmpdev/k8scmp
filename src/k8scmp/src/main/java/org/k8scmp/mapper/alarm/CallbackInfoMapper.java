package org.k8scmp.mapper.alarm;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.k8scmp.monitormgmt.domain.alarm.CallBackInfo;

@Mapper
public interface CallbackInfoMapper {
	
	@Insert("INSERT INTO alarm_callback_info(url, beforeCallbackSms, beforeCallbackMail, afterCallbackSms, afterCallbackMail) VALUES (" +
            "#{url}, #{beforeCallbackSms}, #{beforeCallbackMail}, #{afterCallbackSms}, #{afterCallbackMail})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addCallBackInfo(CallBackInfo callBackInfo);

	@Select("SELECT * FROM alarm_callback_info WHERE id IN " +
            "(SELECT callbackId FROM alarm_template_info WHERE id=#{templateId})")
    CallBackInfo getCallbackInfoByTemplateId(@Param("templateId") int templateId);

	@Delete("DELETE FROM alarm_callback_info WHERE id IN " +
            "(SELECT callbackId FROM alarm_template_info WHERE id=#{templateId})")
    int deleteCallbackInfoByTemplateId(@Param("templateId") int templateId);
}
