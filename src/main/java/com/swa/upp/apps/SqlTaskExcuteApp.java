package com.swa.upp.apps;



import com.swa.upp.beans.TagInfo;
import com.swa.upp.beans.TaskInfo;
import com.swa.upp.beans.TaskTagRule;
import com.swa.upp.constants.TypeCodeConstant;
import com.swa.upp.utils.MysqlDBService;
import com.swa.upp.utils.PropertiesUtil;
import com.swa.upp.utils.SqlTaskExecuteUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SqlTaskExcuteApp {
    public static void main(String[] args) throws IOException {


        String taskId="2";
        String do_date = "2020-06-14";

        //1.先从数据库中查询要计算的task及task对应的标签信息和标签规则
        SqlSessionFactory sqlSessionFactory = SqlTaskExecuteUtil.createSqlSessionFactory("mysql-config.xml");
        SqlSession sqlSession = sqlSessionFactory.openSession();
        MysqlDBService mysqlDBService = new MysqlDBService(sqlSession);

        TaskInfo task = mysqlDBService.getTaskInfoByTaskId(taskId);
        TagInfo tagInfo = mysqlDBService.getTagInfoByTaskId(taskId);
        List<TaskTagRule> taskTagRules = mysqlDBService.getTaskTagRuleListByTaskId(taskId);
//测试用：
//        System.out.println(task);
//        System.out.println(tagInfo);
//        System.out.println(taskTagRules);

        //2.准备一个SparkSession
        SparkSession sparkSession = SqlTaskExecuteUtil.createSparkSession("SqlTaskExecuteApp");

        //3.准备sql,注释2运行看得出的语句能否在hive上运行
        String tableSql = createTableSql(tagInfo, task);
        String insertSql = createInsertSql(tagInfo,do_date,task,taskTagRules);

        //4.执行
        sparkSession.sql(tableSql);
        sparkSession.sql(insertSql);



//        //测试createInsertSql方法
//        createInsertSql(tagInfo,do_date,task,taskTagRules);

    }




    //用于生产建表的sql语句
    public static String createTableSql(TagInfo tagInfo,TaskInfo task){

        //定义模板
        String template="create external table if not exists %s.%s(uid string, tag_value %s) " +
                "comment '%s' " +
                "partitioned by (dt string) " +
                "row format delimited fields terminated by '\\t' " +
                "location '%s/%s'";

        /*
                确定tab_value的类型
                平台可选的标签值的类型就四种:  文本，浮点，日期，整数
                Tag_Info中的tag_value_type就代表当前tab_value的类型，例如 2就是浮点型，3是字符串
         */
        String tagValueType = null;

        if (TypeCodeConstant.TAG_VALUE_TYPE_DATE.equals(tagInfo.getTagValueType())) {
            tagValueType = "string";
        } else if (TypeCodeConstant.TAG_VALUE_TYPE_LONG.equals(tagInfo.getTagValueType())) {
            tagValueType = "bigint";
        } else if (TypeCodeConstant.TAG_VALUE_TYPE_STRING.equals(tagInfo.getTagValueType())) {
            tagValueType = "string";
        } else if (TypeCodeConstant.TAG_VALUE_TYPE_DECIMAL.equals(tagInfo.getTagValueType())) {
            tagValueType = "decimal(16,0)";
        }


        String updbname = PropertiesUtil.getValue("updbname");
        //以标签名小写作为表名
        String tableName = tagInfo.getTagCode().toLowerCase();
        //注释
        String comment = task.getTaskComment();
        String hdfsPath = PropertiesUtil.getValue("hdfsPath");

        //填充占位符
        String tableSql = String.format(template, updbname, tableName, tagValueType, comment, hdfsPath, tableName);

        System.out.println("建表语句:"+tableSql);

        return tableSql;


    }



    //------------把查出来的结果装载
    /*
insert overwrite table up220509.tag_person_nature_gender partition (dt='2020-06-14')
--把计算的tag_value映射为标签名  如果是字符串，查询映射规则，例如M对应男性 。如果是数值类型，把计算的数值导入即可。
select
   id uid, nvl(gender,'U') tag_value
from gmall.dim_user_zip
where dt='9999-12-31';
    */
    public static String createInsertSql(TagInfo tagInfo,String do_date,TaskInfo task,List<TaskTagRule> taskTagRules){

        String template = "insert overwrite table %s.%s partition (dt='%s') " +
                "select uid,%s from ( %s  )tmp";

        //确认库名和表名
        String updbname = PropertiesUtil.getValue("updbname");
        String tableName = tagInfo.getTagCode().toLowerCase();

        //计算的sql已经由画像平台定义号，存储在数据库中
        //如果加了;，去掉
        String taskSql = task.getTaskSql().replace(';',' ').replace("$do_date",do_date);

        /*
            根据子集中查询的结果，进行映射。如果是字符串，查询映射规则，例如M对应男性 。如果是数值类型，把计算的数值导入即可。

    case  tag_value
        when 'M' then '男性'
        when 'F' then '女性'
        when 'U' then '未知'
    end tag_value
         */
        //如果这个task的结果需要根据规则映射，那么 taskTagRules 中一定可以查询到规则

        String tagValueSql = "";

        if (taskTagRules.size() > 0){
            //需要映射  map
            List<String> whenConditionStrs = taskTagRules.stream()
                    .map(taskTagRule -> String.format("  when '%s' then '%s'  ", taskTagRule.getQueryValue(), taskTagRule.getSubTagValue()))
                    .collect(Collectors.toList());

            //一个集合中的三个字符串，拼接为1行，再在前后拼接上case 和 end
            tagValueSql = "case tag_value " + StringUtils.join(whenConditionStrs, ' ') + " end tag_value ";

        }else{
            //无需映射直接把值作为字段
            tagValueSql = " tag_value ";

        }

        String insertSql = String.format(template, updbname, tableName, do_date, tagValueSql, taskSql);

        System.out.println(insertSql);

        return insertSql;

    }















}

