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

    @Delete("delete from serviceversion serviceId = #{serviceId}")
    int deleteAllVersion(@Param("serviceId") String serviceId);

    @Select("select " + BASIC_COLUMNS + " from serviceversion where serviceId = #{serviceId} and version=#{version}")
    VersionBase getVersion(@Param("serviceId") String serviceId, @Param("version") int version);

    @Select("select " + BASIC_COLUMNS + " from serviceversion where serviceId = #{serviceId}")
    List<VersionBase> getAllVersionByServiceId(@Param("serviceId") String serviceId);

    @Select("SELECT MAX(version) FROM serviceversion WHERE serviceId = #{serviceId}")
    Integer getMaxVersion(@Param("serviceId") String serviceId);

    @Update("update serviceversion set data = #{data} where id = #{id}")
    int updateLabelSelector(@Param("id") String id, @Param("data") String data);
}
//
