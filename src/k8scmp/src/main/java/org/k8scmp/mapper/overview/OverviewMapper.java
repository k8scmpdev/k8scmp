package org.k8scmp.mapper.overview;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.k8scmp.overview.domain.OverviewCountInfo;

import java.util.List;

/**
 */
@Mapper
public interface OverviewMapper {
	String BASIC_COLUMN =  " id, resourceId, resourceType, operation, userId, userName, status, message, operateTime";

    @Select("select state as countName,count(state) as count from application GROUP BY state")
    List<OverviewCountInfo> getAppCountInfo();
    
    @Select("select state as countName,count(state) as count from service GROUP BY state")
    List<OverviewCountInfo> getServiceCountInfo();

}
