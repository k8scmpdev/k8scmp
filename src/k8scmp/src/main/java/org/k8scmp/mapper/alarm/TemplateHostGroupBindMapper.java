package org.k8scmp.mapper.alarm;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TemplateHostGroupBindMapper {

	@Insert("INSERT INTO alarm_template_host_group_bind(templateId, hostGroupId, bindTime) VALUES (" +
            "#{templateId}, #{hostGroupId}, #{bindTime})")
    int addTemplateHostGroupBind(@Param("templateId") int templateId, @Param("hostGroupId") int hostGroupId, @Param("bindTime") String bindTime) ;

	@Delete("DELETE FROM alarm_template_host_group_bind WHERE templateId=#{templateId}")
    int deleteTemplateHostGroupBindByTemplateId(@Param("templateId") int templateId);

	@Delete("DELETE FROM alarm_template_host_group_bind WHERE hostGroupId=#{hostGroupId}")
	int deleteTemplateHostGroupBindByHostGroupId(@Param("hostGroupId") int hostGroupId);
}
