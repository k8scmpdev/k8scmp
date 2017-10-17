package org.k8scmp.mapper.appmgmt;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.k8scmp.appmgmt.domain.DeployEvent;
import org.k8scmp.model.DeployEventStatus;

@Mapper
public interface ServiceEventMapper {
	String BASIC_COLUMNS = " id, serviceId, state, operation, content, startTime, expireTime, operatorId";
	@Insert("INSERT INTO serviceEvent ("+ BASIC_COLUMNS +") values" +
            "(#{item.id}, #{item.serviceId}, #{item.state}, #{item.operation}, #{data}, #{item.startTime}, #{item.expireTime}, #{item.operatorId})")
    void createEvent(@Param("item") DeployEvent item, @Param("data") String data);

    @Select("SELECT "+ BASIC_COLUMNS +" FROM serviceEvent WHERE id=#{id}")
    DeployEvent getEvent(@Param("id") String id);

    @Select("SELECT "+ BASIC_COLUMNS +" FROM serviceEvent WHERE serviceId=#{serviceId} order by startTime desc limit 1")
    DeployEvent getNewestEvent(@Param("serviceId") String serviceId);

    @Select("SELECT "+ BASIC_COLUMNS +" FROM serviceEvent WHERE serviceId=#{serviceId}")
    List<DeployEvent> getEventByServiceId(@Param("serviceId") String serviceId);

    @Update("UPDATE serviceEvent set content=#{content}, state=#{status}, expireTime=#{expireTime} WHERE id=#{id}")
    void updateEvent(@Param("id") String id, @Param("status") DeployEventStatus status,
                     @Param("expireTime") String expireTime, @Param("content") String content);

    @Select("SELECT "+ BASIC_COLUMNS +" FROM serviceEvent WHERE state not in ('SUCCESS', 'FAILED', 'ABORTED')")
    List<DeployEvent> getUnfinishedEvent();

    @Select("SELECT se.* FROM serviceEvent se "
            + " JOIN service s ON s.id = se.serviceId "
            + " WHERE s.appId IN ${idList} "
            + " AND de.startTime >= #{startTime}")
    List<DeployEvent> listRecentEventByAppIdTime(@Param("idList") String idList, @Param("startTime") String startTime);

    @Select("SELECT se.* FROM serviceEvent se "
            + " JOIN sevice s ON s.id = se.serviceId "
            + " WHERE se.startTime >= #{startTime}")
    List<DeployEvent> listServiceExistedEventByTime(@Param("startTime") String startTime);

}
