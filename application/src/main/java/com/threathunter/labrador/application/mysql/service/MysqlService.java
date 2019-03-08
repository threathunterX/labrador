package com.threathunter.labrador.application.mysql.service;

import com.threathunter.labrador.application.mysql.domain.Notice;
import com.threathunter.labrador.application.mysql.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by wanbaowang on 17/11/1.
 */
@Service
public class MysqlService {

    @Autowired
    private NoticeMapper noticeMapper;


    public int noticeCheck(Notice notice) {
        return noticeMapper.noticeCheck(notice);
    }

}
