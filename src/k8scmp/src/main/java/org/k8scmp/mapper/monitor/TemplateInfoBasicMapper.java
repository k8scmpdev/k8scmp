package org.k8scmp.mapper.monitor;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.k8scmp.monitormgmt.domain.alarm.TemplateInfoBasic;

@Mapper
public interface TemplateInfoBasicMapper {

	@Select("SELECT * FROM alarm_template_info WHERE isRemoved = 0 AND templateName=#{templateName}")
    TemplateInfoBasic getTemplateInfoBasicByName(@Param("templateName") String templateName);

	@Insert("INSERT INTO alarm_template_info(templateName, templateType, creatorId, creatorName, createTime, updateTime) VALUES (" +
            "#{templateName}, #{templateType}, #{creatorId}, #{creatorName}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addTemplateInfoBasic(TemplateInfoBasic templateInfoBasic);

	@Update("UPDATE alarm_template_info SET deployId=#{deployId} WHERE id=#{id}")
    int setTemplateDeployIdByTemplateId(@Param("id") long id, @Param("deployId") long deployId);

	@Update("UPDATE alarm_template_info SET callbackId=#{callbackId} WHERE id=#{id}")
    int setTemplateCallbackIdByTemplateId(@Param("id") long id, @Param("callbackId") long callbackId);

	@Select("SELECT * FROM alarm_template_info WHERE isRemoved = 0 ORDER BY createTime DESC")
	List<TemplateInfoBasic> listTemplateInfoBasic();
	
	@Select("SELECT * FROM alarm_template_info WHERE isRemoved = 0 AND id=#{id}")
    TemplateInfoBasic getTemplateInfoBasicById(@Param("id") long id);

	@Update("UPDATE alarm_template_info SET templateName=#{templateName}, updateTime=#{updateTime} WHERE id=#{id}")
    int updateTemplateInfoBasicById(TemplateInfoBasic templateInfoBasic);

	@Update("UPDATE alarm_template_info SET isRemoved = 1 WHERE id=#{id}")
    int deleteTemplateInfoBasicById(@Param("id") long id);

	@Select("SELECT * FROM alarm_template_info LEFT OUTER JOIN alarm_template_host_group_bind ON " +
            "alarm_template_info.id = alarm_template_host_group_bind.templateId WHERE alarm_template_host_group_bind.hostGroupId " +
            "= #{hostGroupId} AND alarm_template_info.isRemoved = 0 order by alarm_template_host_group_bind.bindTime")
    List<TemplateInfoBasic> getTemplateInfoBasicByHostGroupId(@Param("hostGroupId") long hostGroupId);
	
}
