package com.swa.upp.utils;

import com.swa.upp.beans.TagInfo;
import com.swa.upp.beans.TaskInfo;
import com.swa.upp.beans.TaskTagRule;
import com.swa.upp.mappers.TagInfoMapper;
import com.swa.upp.mappers.TaskInfoMapper;
import com.swa.upp.mappers.TaskTagRuleMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.commons.lang3.StringUtils;
import java.util.List;

//编写一个服务类直接调用我们的Dao操作数据库
public class MysqlDBService
{

    private TagInfoMapper tagInfoMapper;
    private TaskInfoMapper taskInfoMapper;
    private TaskTagRuleMapper taskTagRuleMapper;


    public MysqlDBService(SqlSession sqlSession ) {

        this.tagInfoMapper =sqlSession.getMapper(TagInfoMapper.class);
        this.taskInfoMapper =sqlSession.getMapper(TaskInfoMapper.class);
        this.taskTagRuleMapper =sqlSession.getMapper(TaskTagRuleMapper.class);

    }

    //查询当前所有要执行的任务
    public List<TaskInfo> getAllTaskNeedToExecute(){
        return taskInfoMapper.getAllTaskNeedToExecute();
    }


    public TagInfo getTagInfoByTaskId(String id) {

        if (StringUtils.isBlank(id)){//如果是Integer类型我们可以直接看非空或者为负数
            throw new RuntimeException("参数非法!");
        }
        return tagInfoMapper.getTagInfoByTaskId(id);
    }

    public TaskInfo getTaskInfoByTaskId(String taskId) {

        if (StringUtils.isBlank(taskId)){
            throw new RuntimeException("参数非法!");
        }

        return taskInfoMapper.getTaskInfoByTaskId(taskId);
    }

    public List<TaskTagRule> getTaskTagRuleListByTaskId(String taskId) {
        if (StringUtils.isBlank(taskId)){
            throw new RuntimeException("参数非法!");
        }
        return taskTagRuleMapper.getTaskTagRuleListByTaskId(taskId);
    }
}
