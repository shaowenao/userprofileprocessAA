package com.swa.upp.mappers;




import com.swa.upp.beans.TagInfo;

import java.util.List;

public interface TagInfoMapper
{
    //根据句id查询Taginfo
    TagInfo getTagInfoByTaskId(String id);

    List<TagInfo> getTagInfoListWithOn();
}
