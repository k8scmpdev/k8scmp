package org.k8scmp.mapper.monitor;

import org.apache.ibatis.annotations.*;
import org.k8scmp.monitormgmt.domain.monitor.MonitorTarget;


/**
 * Created by baokangwang on 2016/3/7.
 */
@Mapper
public interface MonitorTargetMapper {
    @Insert("INSERT INTO monitor_targets (target, create_time) VALUES (" +
            "#{target}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int addMonitorTarget(MonitorTarget MonitorTarget);

    @Update("UPDATE monitor_targets SET target=#{target}, create_time=#{createTime} WHERE id=#{id}")
    int updateMonitorTargetById(MonitorTarget MonitorTarget);

    @Select("SELECT target FROM monitor_targets WHERE id = #{id}")
    String getMonitorTargetById(@Param("id") long id);
}