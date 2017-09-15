package org.k8scmp.operation;


import java.util.List;

/**
 */
public interface OperationLog {

    void insertRecord(OperationRecord record);

    void updateStatus(int id, String status);

    OperationRecord getById(int id);

    List<OperationRecord> listOperationRecordByUserNameTime(Integer userId, long operateTime);

	List<OperationRecord> listOperationRecord4Overview();
}
