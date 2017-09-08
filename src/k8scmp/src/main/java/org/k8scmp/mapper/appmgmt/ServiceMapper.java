package org.k8scmp.mapper.appmgmt;

import java.util.List;

import org.apache.ibatis.annotations.*;
import org.k8scmp.appmgmt.domain.ServiceConfigInfo;
import org.k8scmp.appmgmt.domain.ServiceInfo;

/**
 * Created by Yanhl on 2017/8/24.
 */
@Mapper
public interface ServiceMapper {
	String BASIC_COLUMN =  "  id, serviceCode, appId, startSeq, description, state, data, createTime, creatorId, lastModifiedTime, lastModifierId ";
//	@Select({"<script>","SELECT s.id, s.serviceCode, sa.appId, s.description, s.state, s.data, s.createTime, s.creatorId, s.lastModifiedTime, s.lastModifierId FROM service s inner join serviceapp sa on s.id = sa.serviceId where 1=1 ",
//	"<when test='item!=null'>",
//		"<when test='item.appId!=null and item.appId!=&quot;&quot;'>",
//		"and sa.appId = #{item.appId} ", "</when>",
//		"<when test='item.serviceCode!=null and item.serviceCode!=&quot;&quot;'>",
//		"and s.serviceCode = #{item.serviceCode} ", "</when>",
//	"</when>","order by s.createTime desc","</script>"})
//    List<ServiceInfo> getServices(@Param("item") ServiceInfo item);
//	
	@Select({"<script>","SELECT" + BASIC_COLUMN + "FROM service where 1=1 ",
		"<when test='item!=null'>",
			"<when test='item.serviceCode!=null and item.serviceCode!=&quot;&quot;'>",
			"and serviceCode like '%${item.serviceCode}%' ", "</when>",
			"<when test='item.appId!=null and item.appId!=&quot;&quot;'>",
			"and appId=#{item.appId} ", "</when>",
			"<when test='item.state!=null and item.state!=&quot;&quot;'>",
			"and state=#{item.state} ", "</when>",
		"</when>","order by createTime desc","</script>"})
    List<ServiceInfo> getServices(@Param("item") ServiceInfo item);
	
	@Select("SELECT BASIC_COLUMN FROM service"
			+" where appId=#{appId}")
    List<ServiceInfo> getServicesByAppId(@Param("appId") String appId);
	
	@Select("SELECT BASIC_COLUMN FROM service"
			+" where id=#{id}")
	ServiceInfo getService(@Param("id") String id);
	
    @Insert("INSERT INTO service(BASIC_COLUMN) values (" +
            " #{item.id}, #{item.serviceCode}, #{item.appId}, #{item.startSeq}, #{item.description}, #{item.state}, #{item.data}," +
            " #{item.createTime},#{item.creatorId},#{item.lastModifiedTime},#{item.lastModifierId})")
    int createService(@Param("item") ServiceConfigInfo item,@Param("data") String data);

    @Update("update service" +
            " set description=#{item.description},lastModifiedTime=#{item.lastModifiedTime},lastModifierId=#{item.lastModifierId}" +
            "where id = #{item.id}")
    int updateService(@Param("item") ServiceInfo item);
    
    @Delete("delete from service where id in(${id})")
	int deleteService(@Param("id") String id);
    
    @Update("update service set startSeq=#{startSeq} where id=#{id}")
    int updateServiceStartSeq(@Param("item") ServiceInfo item);
    
    @Select("SELECT state FROM service"
			+" where id=#{id}")
    String getServiceStatu(@Param("id") String id);
    
    @Update("update service set state=#{state} where id=#{id}")
    int updateServiceStatu(@Param("state") String state,@Param("id") String id);

    
}
