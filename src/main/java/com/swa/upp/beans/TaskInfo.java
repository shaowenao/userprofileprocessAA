package com.swa.upp.beans;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TaskInfo {
//映射任务信息表
    Long id;
    String taskName;
    String taskStatus;
    String taskComment;
    String taskType;
    String execType;
    String mainClass;
    Long fileId;
    String taskArgs;
    String taskSql;
    Long taskExecLevel;
    Timestamp createTime; //时间戳带时分秒用这个好
}
