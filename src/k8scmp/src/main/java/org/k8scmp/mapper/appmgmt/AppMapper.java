package org.k8scmp.mapper.appmgmt;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.k8scmp.appmgmt.domain.AppInfo;

/**
 * Created by Yanhl on 2017/8/24.
 */
@Mapper
public interface AppMapper {
	String BASIC_COLUMN =  " id, appId, namespace, logicClusterId, clusterId, description, state, createTime, creatorId, lastModifiedTime, lastModifierId ";
	@Select({"<script>","SELECT" + BASIC_COLUMN + "from application where 1=1 ","<when test='item!=null'>",
			"<when test='item.appId!=null and item.appId!=&quot;&quot;'>",
			"and appId like '%${item.appId}%' ", "</when>",
		"</when>","order by createTime desc","</script>"})
    List<AppInfo> getApps(@Param("item") AppInfo item);
	
    @Insert("INSERT INTO (BASIC_COLUMN) values (" +
            " #{item.id}, #{item.appId}, #{item.namespace}, #{item.logicClusterId}, #{item.clusterId}," +
            " #{item.description}, #{item.state},#{item.createTime},#{item.creatorId},#{item.lastModifiedTime},#{item.lastModifierId})")
    int createApp(@Param("item") AppInfo item);

    @Update("update application" +
            " set appId=#{item.appId}, description=#{item.description} " +
            "where id = #{item.id}")
    int updateApp(@Param("item") AppInfo item);
    
    @Delete("delete from application where id = #{item.id}")
	int deleteApp(@Param("id") String id);
}
