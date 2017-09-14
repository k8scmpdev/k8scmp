package org.k8scmp.mapper.monitor.portal;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.k8scmp.monitormgmt.domain.alarm.falcon.Template;

@Mapper
public interface PortalTemplateMapper {
	
    @Insert("INSERT INTO tpl (id, tpl_name, action_id, create_user, create_at) VALUES (" +
            "#{id}, #{tpl_name}, #{action_id}, #{create_user}, #{create_at})")
    int insertTemplateById(Template template);

    @Update("UPDATE tpl SET tpl_name=#{tpl_name}, action_id=#{action_id} WHERE id=#{id}")
    int updateTemplateById(Template template);

    @Select("SELECT * FROM tpl WHERE id=#{id}")
    Template getTemplateById(@Param("id") long id);

    @Delete("DELETE FROM tpl WHERE id=#{id}")
    int deleteTemplateById(@Param("id") long id);
}
