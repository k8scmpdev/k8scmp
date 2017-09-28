package org.k8scmp.mapper.appmgmt;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.k8scmp.appmgmt.domain.AppInfo;

/**
 * Created by Yanhl on 2017/8/24.
 */
@Mapper
public interface AppMapper {
	String BASIC_COLUMN =  " id, appCode, namespace, hostLabel, clusterId, description, state, createTime, creatorId, lastModifiedTime, lastModifierId ";
	@Select({"<script>","SELECT" + BASIC_COLUMN + "from application where 1=1 ","<when test='item!=null'>",
			"<when test='item.appCode!=null and item.appCode!=&quot;&quot;'>",
			"and appCode like '%${item.appCode}%' ", "</when>",
		"</when>","order by createTime desc","</script>"})
    List<AppInfo> getApps(@Param("item") AppInfo item);
	
    @Insert("INSERT INTO application(" + BASIC_COLUMN + " ) values (" +
            " #{item.id}, #{item.appCode}, #{item.namespace}, #{item.hostLabel}, #{item.clusterId}," +
            " #{item.description}, #{item.state},#{item.createTime},#{item.creatorId},#{item.lastModifiedTime},#{item.lastModifierId})")
    int createApp(@Param("item") AppInfo item);

    @Update("update application" +
            " set appCode=#{item.appCode}, description=#{item.description} ,namespace=#{item.namespace}, hostLabel=#{item.hostLabel}, clusterId=#{item.clusterId},lastModifiedTime=#{item.lastModifiedTime},lastModifierId=#{item.lastModifierId})" +
            "where id = #{item.id}")
    int updateApp(@Param("item") AppInfo item);
    
    @Delete("delete from application where id = #{id}")
	int deleteApp(@Param("id") String id);
    
    @Select("select"+BASIC_COLUMN+" from application where id=#{id}")
    AppInfo getApp(@Param("id") String id);
    
    @Select("select"+BASIC_COLUMN+" from application where id in(select appId from service where serviceCode=#{serviceCode})")
    List<AppInfo> getAppsByserviceCode(@Param("serviceCode") String serviceCode);
}
