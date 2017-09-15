package org.k8scmp.mapper.operation;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.k8scmp.operation.OperationRecord;

import java.util.List;

/**
 */
@Mapper
public interface OperationMapper {
	String BASIC_COLUMN =  " id, resourceId, resourceType, operation, userId, userName, status, message, operateTime";
    @Insert("insert into operation_history (id, resourceId, resourceType, operation, userId, userName, status, message, operateTime)" +
            " values (#{id}, #{resourceId}, #{resourceType}, #{operation}, #{userId}, #{userName}, #{status}, #{message}, #{operateTime})")
    int insertRecord(OperationRecord record);

    @Select("select"+BASIC_COLUMN+" from operation_history where id = #{id}")
    OperationRecord getById(int id);

    //@Todo resourceType needed to be removed
    @Select("SELECT"+BASIC_COLUMN+" FROM operation_history WHERE userId = #{userId} AND operateTime >= #{operateTime}"
            + " AND resourceType in ('PROJECT','PROJECT_COLLECTION'"
            + ",'DEPLOY','DEPLOY_COLLECTION'"
            + ",'CONFIGURATION','CONFIGURATION_COLLECTION'"
            + ",'CLUSTER','LOADBALANCER','LOADBALANCER_COLLECTION')")
    List<OperationRecord> listOperationRecordByUserNameTime(@Param("userId") Integer userId, @Param("operateTime") long operateTime);
    
    @Select("SELECT "+BASIC_COLUMN+" FROM operationLog order by operateTime desc limit 6")
    List<OperationRecord> listOperationRecord4Overview();
}
