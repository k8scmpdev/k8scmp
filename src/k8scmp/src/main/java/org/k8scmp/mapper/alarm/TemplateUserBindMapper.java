package org.k8scmp.mapper.alarm;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TemplateUserBindMapper {

	@Insert("INSERT INTO alarm_template_user_bind(templateId, userId, bindTime) VALUES (" +
            "#{templateId}, #{userId}, #{bindTime})")
    int addTemplateUserGroupBind(@Param("templateId") int templateId, @Param("userId") int userId, @Param("bindTime") String bindTime);

	@Select("SELECT userId FROM alarm_template_user_bind WHERE templateId=#{templateId}")
    List<Long> listUserIdByTemplateId(@Param("templateId") int templateId);

	@Delete("DELETE FROM alarm_template_user_bind WHERE templateId=#{templateId}")
    int deleteTemplateUserBindByTemplateId(@Param("templateId") int templateId);
	
}
