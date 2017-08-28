package org.k8scmp.mapper.appmgmt;

import org.apache.ibatis.annotations.*;
import org.k8scmp.appmgmt.domain.AppInfo;

/**
 * Created by KaiRen on 2016/9/20.
 */
@Mapper
public interface AppMapper {
    @Insert("INSERT INTO " +
            " (name, description, state, createTime, removeTime, removed, data) values (" +
            " #{item.name}, #{item.description}, #{item.state}, #{item.createTime}, #{item.removeTime}," +
            " #{item.removed}, #{data})")
    @Options(useGeneratedKeys = true, keyProperty = "item.id", keyColumn = "id")
    int createApp(@Param("item") AppInfo item, @Param("data") String data);

    @Update("update " +
            " set name=#{item.name}, description=#{item.description}, state=#{item.state}, " +
            "data=#{data} where id = #{item.id}")
    int updateApp(@Param("item") AppInfo item, @Param("data") String data);
}
