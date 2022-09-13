package com.swa.upp.beans;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TagInfo {
//映射存标签的表
    Long id;
    String tagCode;
    String tagName;
    Long tagLevel;
    Long parentTagId;
    String tagType;
    String tagValueType;
    Long tagTaskId;
    String tagComment;
    Timestamp createTime;
    Timestamp updateTime;
    
}
