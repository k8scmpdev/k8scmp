package org.k8scmp.monitormapper.portal;

import org.apache.ibatis.annotations.*;
import org.k8scmp.monitormgmt.domain.alarm.asist.Link;

/**
 * Created by baokangwang on 2016/4/14.
 */
@Mapper
public interface LinkMapper {

    @Insert("INSERT INTO alarm_link_info(content) VALUES (#{content})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addLink(Link link);

    @Select("SELECT * FROM alarm_link_info WHERE id=#{id}")
    Link getLinkById(@Param("id") int id);
}
