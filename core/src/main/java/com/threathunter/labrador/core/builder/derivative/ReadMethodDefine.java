package com.threathunter.labrador.core.builder.derivative;

import com.threathunter.labrador.common.model.Variable;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.common.util.ThreadLocalDateUtil;
import com.threathunter.labrador.core.exception.LabradorException;
import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wanbaowang on 17/11/23.
 */
public interface ReadMethodDefine {

    public static List<String> getDuring(EnumUtil.PeriodType periodType, int startOffset, int endOffset) {
        List<String> periods = new ArrayList<>();
        //天
        if(periodType  == EnumUtil.PeriodType.last_n_days) {
            Date today = new Date();
            Date endDay = DateUtils.addDays(today, endOffset);
            Date startDay = DateUtils.addDays(today, startOffset);
            List<Date> dateList = ThreadLocalDateUtil.getDaysBetweenDates(startDay, endDay);
            for(Date date: dateList) {
                periods.add(ThreadLocalDateUtil.formatDay(date));
            }
        } else {
            Date currentHour = new Date();
            Date endHour =  DateUtils.addHours(currentHour, endOffset);
            Date startHour = DateUtils.addHours(currentHour, startOffset);
            List<Date> hourList = ThreadLocalDateUtil.getDaysBetweenHours(startHour, endHour);
            for(Date hour : hourList) {
                periods.add(ThreadLocalDateUtil.formatDayHour(hour));
            }
        }
        return periods;
    }

    public Object read(Object sourceValue, Variable sourceVariable) throws LabradorException;

}
