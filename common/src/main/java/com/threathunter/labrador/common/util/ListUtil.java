package com.threathunter.labrador.common.util;

import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ListUtil {

    public static <T> List<T> copy(List<T> source) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException  {
        //clone后的集合
        List<T> temp=new ArrayList<T>();

        for(T t:source){

            //T temporary=(T) source.getClass().newInstance();
            //BeanUtils.copyProperties(temporary,t);//Spring BeanUtils or Apache Commons
            T temporary=(T) BeanUtils.cloneBean(t);
            temp.add(temporary);
        }
        return temp;
    }
}
