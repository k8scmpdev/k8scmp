package org.k8scmp.operation.impl;

import org.k8scmp.mapper.operation.OperationMapper;
import org.k8scmp.operation.OperationLog;
import org.k8scmp.operation.OperationRecord;
import org.k8scmp.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 */
@Service("operationLog")
public class OperationLogImpl implements OperationLog {
    @Autowired
    OperationMapper mapper;

    @Override
    public void insertRecord(OperationRecord record) {
    	record.setId(UUIDUtil.generateUUID());
        mapper.insertRecord(record);
    }

    @Override
    public void updateStatus(int id, String status) {

    }

    @Override
    public OperationRecord getById(int id) {
        return mapper.getById(id);
    }

    @Override
    public List<OperationRecord> listOperationRecordByUserNameTime(Integer userId, long operateTime) {
        return mapper.listOperationRecordByUserNameTime(userId, operateTime);
    }
    
    @Override
    public List<OperationRecord> listOperationRecord4Overview() {
        return mapper.listOperationRecord4Overview();
    }
    
    @Override
    public List<OperationRecord> listAllOperationRecord4Overview() {
        return mapper.listAllOperationRecord4Overview();
    }
    
    @Override
    public List<OperationRecord> listAllOperationRecordByKey(String keyword,String rtype,String otype,String status) {
        return mapper.listAllOperationRecordByKey(keyword,rtype,otype,status);
    }
}
