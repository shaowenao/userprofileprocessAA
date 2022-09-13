package com.swa.upp.mappers;

import com.swa.upp.beans.TaskInfo;

import java.util.List;

public interface TaskInfoMapper
{
    //需求一
    TaskInfo getTaskInfoByTaskId(String taskId);

    //查询当前所有需要执行的任务
    List<TaskInfo> getAllTaskNeedToExecute();
}
