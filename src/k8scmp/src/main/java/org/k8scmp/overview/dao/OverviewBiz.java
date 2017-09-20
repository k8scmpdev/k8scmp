package org.k8scmp.overview.dao;

import org.k8scmp.overview.domain.OverviewCountInfo;

import java.util.List;

/**
 * Created by feiliu206363 on 2016/1/20.
 */
public interface OverviewBiz {

    List<OverviewCountInfo> getAppInfo();

	List<OverviewCountInfo> getServiceInfo();
    
}
