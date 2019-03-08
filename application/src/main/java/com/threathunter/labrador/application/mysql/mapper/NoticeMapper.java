package com.threathunter.labrador.application.mysql.mapper;

import com.threathunter.labrador.application.mysql.domain.Notice;
import org.apache.ibatis.annotations.Param;

/**
 * Created by wanbaowang on 17/11/1.
 */
public interface NoticeMapper {

    public int noticeCheck(Notice notice);


}
