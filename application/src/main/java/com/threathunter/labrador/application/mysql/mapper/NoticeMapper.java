package com.threathunter.labrador.application.mysql.mapper;

import com.threathunter.labrador.application.mysql.domain.Notice;
import org.apache.ibatis.annotations.Param;

/**
 * 
 */
public interface NoticeMapper {

    public int noticeCheck(Notice notice);


}
