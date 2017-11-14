package org.k8scmp.mapper.alarm;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TemplateUserBindMapper {

	@Insert("INSERT INTO alarm_template_user_group_bind(templateId, userGroupId, bindTime) VALUES (" +
            "#{templateId}, #{userGroupId}, #{bindTime})")
    int addTemplateUserGroupBind(@Param("templateId") int templateId, @Param("userGroupId") int userGroupId, @Param("bindTime") String bindTime);

	@Select("SELECT userGroupId FROM alarm_template_user_group_bind WHERE templateId=#{templateId}")
    List<Integer> listUserIdByTemplateId(@Param("templateId") int templateId);

	@Delete("DELETE FROM alarm_template_user_group_bind WHERE templateId=#{templateId}")
    int deleteTemplateUserBindByTemplateId(@Param("templateId") int templateId);
	
}
