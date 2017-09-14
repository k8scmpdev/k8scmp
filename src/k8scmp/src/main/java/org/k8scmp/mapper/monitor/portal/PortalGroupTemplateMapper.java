package org.k8scmp.mapper.monitor.portal;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.k8scmp.monitormgmt.domain.alarm.falcon.GroupTemplate;

@Mapper
public interface PortalGroupTemplateMapper {

	@Delete("DELETE FROM grp_tpl WHERE grp_id=#{grp_id}")
    int deleteByHostGroup(@Param("grp_id") long grp_id);

    @Delete("DELETE FROM grp_tpl WHERE tpl_id=#{tpl_id}")
    int deleteByTemplate(@Param("tpl_id") long tpl_id);

    @Insert("INSERT INTO grp_tpl (grp_id, tpl_id, bind_user) VALUES (#{grp_id}, #{tpl_id}, #{bind_user})")
    int insertGroupTemplateBind(GroupTemplate groupTemplate);

    @Select("SELECT grp_id FROM grp_tpl WHERE grp_id=#{grp_id} AND tpl_id=#{tpl_id}")
    Integer checkGroupTemplateBind(GroupTemplate groupTemplate);

    @Select("SELECT grp_id FROM grp_tpl WHERE tpl_id=#{tpl_id}")
    Integer getGroupIdByTemplateId(@Param("tpl_id") long tpl_id);

}
