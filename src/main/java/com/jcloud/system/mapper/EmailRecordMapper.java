package com.jcloud.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jcloud.system.entity.EmailRecord;
import com.jcloud.system.entity.EmailRecordEnclosure;
import com.jcloud.system.entity.EmailRecordParam;
import com.jcloud.system.entity.EmailRecordReceiver;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmailRecordMapper extends BaseMapper<EmailRecord> {
    void insertObj(EmailRecord emailRecord);

    void insertReceiver(EmailRecordReceiver emailRecordReceiver);

    void insertEnclosure(EmailRecordEnclosure emailRecordEnclosure);

    EmailRecord findRecordById(int recordId);

    void updateRecord(EmailRecord emailRecord);

    void insertParam(EmailRecordParam emailRecordParam);
}