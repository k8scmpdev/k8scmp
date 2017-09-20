package org.k8scmp.overview.dao.impl;

import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.ClusterInfo;
import org.k8scmp.globalmgmt.domain.GlobalInfo;
import org.k8scmp.globalmgmt.domain.GlobalType;
import org.k8scmp.mapper.global.GlobalMapper;
import org.k8scmp.mapper.overview.OverviewMapper;
import org.k8scmp.overview.dao.OverviewBiz;
import org.k8scmp.overview.domain.OverviewCountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by feiliu206363 on 2016/4/8.
 */
@Service("overviewBiz")
public class OverviewBizImpl implements OverviewBiz {
    @Autowired(required = true)
    OverviewMapper overviewMapper;

    @Override
    public List<OverviewCountInfo> getAppInfo() {
        return overviewMapper.getAppCountInfo();
    }
    
    @Override
    public List<OverviewCountInfo> getServiceInfo() {
        return overviewMapper.getServiceCountInfo();
    }
}
