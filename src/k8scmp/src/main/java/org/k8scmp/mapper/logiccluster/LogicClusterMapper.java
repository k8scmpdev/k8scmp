package org.k8scmp.mapper.logiccluster;

import org.apache.ibatis.annotations.*;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;

import java.util.List;

/**
 * Created by feiliu206363 on 2016/1/20.
 */
@Mapper
public interface LogicClusterMapper {
	//由于前期后台维护不做集群，所以暂时直接这么取
    @Select("SELECT logicName FROM logiccluster WHERE id=1")
    String getLogicClusterNameById();
    
}
