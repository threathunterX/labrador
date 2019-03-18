package com.threathunter.labrador.common.util;


import com.threathunter.labrador.common.exception.BuilderException;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 
 * Labrador
 */
public class EnumUtil {

    public enum Module {
        profile, realtime, slot, base
    }

    public enum ProfileModuleType {
        basic, basic_batch, derived, external_slot
    }

    public enum Source {
        ACCOUNT_TOKEN_CHANGE, ACCOUNT_LOGIN
    }

    public enum Function {
        last, count, first, lastn, distinct, sum, group_count, distinct_count, merge_value, last_value, merge, max, global_merge,
        global_sum, global_map_merge_topn, global_latest;
    }

    public enum PeriodType {
        ever, last_n_days, last_n_hours, hourly, dayly, fromslot
    }

    public enum Dimension {
        uid, did, ip, others, global, empty
    }

    public enum Operation {
        equals
    }

    public enum OperateType {
        or, and
    }

    public enum Status {
        enable, disable
    }

    public enum UpdateType {
        file, http
    }

    public enum IdGenerator {
        file
    }

    public enum FilterType {
        and, or, not, simple
    }

    public enum BabelServer {
        redis, rabbitmq
    }

    public enum PutMethod {
        put, increment, increment_map_period, increment_map_period_second_index, increment_map, increment_map_second_index,
        put_first, put_list_n, put_map_list, put_list_distinct, put_map_list_distinct,
        sum_map_period, sum_map_period_second_index, sum, sum_map,
        batch_merge_map_long, batch_increment_long, batch_put, batch_merge
    }

    public enum GetMethod {
        distinct, count, lastn, group_count, last, sum, distinct_count
    }

    public enum DataType {
        long_type, double_type, string_type, list_type, map_type, mmap_type, mlist_type, empty_type;
    }

    public static Operation parseOperation(String operateValue) throws BuilderException {
        Operation operation = null;
        switch (operateValue) {
            case "==":
                operation = Operation.equals;
                break;
            default:
                throw new BuilderException("unknow operation " + operateValue);
        }

        if (null == operation) {
            throw new BuilderException("unknow operation " + operateValue);
        } else {
            return operation;
        }
    }

    public static boolean isBasicModuleType(ProfileModuleType moduleType) {
        if (moduleType == EnumUtil.ProfileModuleType.basic ||
                moduleType == EnumUtil.ProfileModuleType.basic_batch) {
            return true;
        } else {
            return false;
        }
    }

    public static Dimension parseDimension(String value) throws BuilderException {
        if (StringUtils.isBlank(value)) {
            return Dimension.empty;
        }
        if (EnumUtil.containsEnum(Dimension.class, value)) {
            return Dimension.valueOf(value);
        }
        return Dimension.others;
    }

    public static DataType parseDataType(String dataType) throws BuilderException {
        DataType curDataType = null;
        switch (dataType) {
            case "long":
                curDataType = DataType.long_type;
                break;
            case "double":
                curDataType = DataType.double_type;
                break;
            case "string":
                curDataType = DataType.string_type;
                break;
            case "list":
                curDataType = DataType.list_type;
                break;
            case "":
                curDataType = DataType.empty_type;
                break;
            case "map":
                curDataType = DataType.map_type;
                break;
            case "mmap":
                curDataType = DataType.mmap_type;
                break;
            case "mlist":
                curDataType = DataType.mlist_type;
                break;
        }
        if (null == curDataType) {
            throw new BuilderException("unsupport function data type " + dataType);
        } else {
            return curDataType;
        }
    }


/*    public enum DataType {
        datatype_empty, datatype_double, datatype_long, datatype_string, list_string, list_long, map_list_string, map_list_long, map_long, map_string,unknown
    }*/

    public static boolean containsEnum(Class clazz, String name) {
        List list = EnumUtils.getEnumList(clazz);
        for (Object o : list) {
            if (String.valueOf(o).equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        boolean b = EnumUtil.containsEnum(Source.class, "ACCOUNT_TOKEN_CHANGE");
        System.out.println(b);
    }

}
