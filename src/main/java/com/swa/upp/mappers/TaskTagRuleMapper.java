package com.swa.upp.mappers;

import com.swa.upp.beans.TaskTagRule;

import java.util.List;


//定义一个计算任务时，会计算指定任务的值和四级标签对应
//根据task_id查询某个task的计算结果映射规则
public interface TaskTagRuleMapper
{
    List<TaskTagRule> getTaskTagRuleListByTaskId(String taskId);
}
