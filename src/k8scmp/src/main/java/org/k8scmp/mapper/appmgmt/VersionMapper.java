package org.k8scmp.mapper.appmgmt;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.k8scmp.appmgmt.domain.Version;
import org.k8scmp.appmgmt.domain.VersionBase;

@Mapper
public interface VersionMapper {
	String BASIC_COLUMNS = " id, versionName, description, state, data, createTime, creatorId, lastModifiedTime, lastModifierId";
	@Insert("INSERT INTO serviceversion"+
            " (id, versionName, version, serviceId, description, state, data, createTime, creatorId, lastModifiedTime, lastModifierId) values (" +
            "  #{item.id}, #{item.versionName}, #{item.version}, #{item.serviceId}, #{item.description}, #{item.state}, #{data}, #{item.createTime}, #{item.creatorId}, #{item.lastModifiedTime}, #{item.lastModifierId})")
    int insertVersion(@Param("item") Version item, @Param("data") String data);

    @Delete("delete from serviceversion where serviceId = #{serviceId}")
    int deleteAllVersion(@Param("serviceId") String serviceId);
    
    @Delete("delete from serviceversion where id = #{id}")
    int deleteVersionById(@Param("id") String id);

    @Delete("delete from serviceversion where serviceId = #{serviceId} and version=#{version}")
    int deleteVersionByServiceId(@Param("serviceId") String serviceId, @Param("version") int version);
    
    @Select("select " + BASIC_COLUMNS + " from serviceversion where serviceId = #{serviceId} and version=#{version}")
    VersionBase getVersion(@Param("serviceId") String serviceId, @Param("version") int version);

    @Select("select " + BASIC_COLUMNS + " from serviceversion where serviceId = #{serviceId}")
    List<VersionBase> getAllVersionByServiceId(@Param("serviceId") String serviceId);

    @Select("SELECT MAX(version) FROM serviceversion WHERE serviceId = #{serviceId}")
    Integer getMaxVersion(@Param("serviceId") String serviceId);
    
    @Select("SELECT versionName,version FROM serviceversion WHERE serviceId = #{serviceId}")
    List<Version> getVersionNames(@Param("serviceId") String serviceId);

    @Update("update serviceversion set data = #{data},lastModifierId=#{item.lastModifierId},lastModifiedTime=#{item.lastModifiedTime} where id = #{item.id}")
    int updateVersion(@Param("item") Version item, @Param("data") String data);
}
//
