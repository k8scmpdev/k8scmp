package org.k8scmp.mapper.monitor;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TemplateUserBindMapper {

	@Insert("INSERT INTO alarm_template_user_bind(userGroupId, templateId, bindTime) VALUES (" +
            "#{templateId}, #{userGroupId}, #{bindTime})")
    int addTemplateUserGroupBind(@Param("userGroupId") long userGroupId, @Param("templateId") long templateId, @Param("bindTime") long bindTime);

	@Select("SELECT userId FROM alarm_template_user_bind WHERE templateId=#{templateId}")
    List<Integer> listUserIdByTemplateId(@Param("templateId") int templateId);

	@Delete("DELETE FROM alarm_template_user_bind WHERE templateId=#{templateId}")
    int deleteTemplateUserBindByTemplateId(@Param("templateId") long templateId);
	
}
