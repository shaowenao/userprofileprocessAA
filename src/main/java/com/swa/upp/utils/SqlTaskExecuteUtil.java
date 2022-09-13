package com.swa.upp.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.InputStream;

//01SqlTaskExecuteUtil
public class SqlTaskExecuteUtil
{

    //封装一个可以读取指定的mybaits配置文件，构造SqlSessionFactory的方法
    public static SqlSessionFactory createSqlSessionFactory(String config) throws IOException {
        InputStream inputStream = Resources.getResourceAsStream(config);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory;
    }

    //构造一个SparkSession
    public static SparkSession createSparkSession(String appName){

        SparkConf sparkConf = new SparkConf()
                .setAppName(appName)
                //默认情况下，spark在本地创建库和表，如果希望在hdfs上建表和建表需要加参数
                .set("spark.sql.warehouse.dir",PropertiesUtil.getValue("hiveWarehouse"))
                .setMaster(PropertiesUtil.getValue("masterUrl"));


        SparkSession sparkSession = SparkSession.builder()
                .config(sparkConf)
                .enableHiveSupport()
                .getOrCreate();

        return sparkSession;

    }
}
