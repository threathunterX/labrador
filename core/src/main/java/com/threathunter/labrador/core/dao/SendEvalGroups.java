package com.threathunter.labrador.core.dao;

import com.aerospike.client.Key;
import com.aerospike.client.Value;
import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.common.util.ThreadLocalDateUtil;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.transform.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by wanbaowang on 17/8/29.
 */
public class SendEvalGroups {

    private Map<EnumUtil.PutMethod, AbstractSendEval> sendRegistry = new HashMap<>();

    private static int LIST_DEFAULT_LENGTH = 24;
    //约10天，map中的key存的是小时，两个时间差的天数为24进制，
    //举例：2017111420比2017111320大100，因此10天即1000
    private static int MAP_DEFAULT_LENGTH = 1000;
    private static final Logger logger = LoggerFactory.getLogger(SendEvalGroups.class);

    public SendEvalGroups() throws LabradorException {
        sendRegistry.put(EnumUtil.PutMethod.put, new PutSendEval());
        sendRegistry.put(EnumUtil.PutMethod.increment, new IncrementSendEval());
        sendRegistry.put(EnumUtil.PutMethod.put_first, new PutFirstSendEval());
        sendRegistry.put(EnumUtil.PutMethod.put_list_n, new PutList_N_Eval());
        sendRegistry.put(EnumUtil.PutMethod.put_list_distinct, new PutListDistinct());
        sendRegistry.put(EnumUtil.PutMethod.put_map_list, new PutMapListPeriod());
        sendRegistry.put(EnumUtil.PutMethod.put_map_list_distinct, new PutMapListDistinctPeriod());
        sendRegistry.put(EnumUtil.PutMethod.increment_map_second_index, new IncrementMapSecondIndex());
        sendRegistry.put(EnumUtil.PutMethod.increment_map, new IncrementMap());
        sendRegistry.put(EnumUtil.PutMethod.sum_map_period, new SumMapPeriod());
        sendRegistry.put(EnumUtil.PutMethod.increment_map_period, new IncrementMapPeriod());


        //以下为batchSend的方法
        sendRegistry.put(EnumUtil.PutMethod.batch_put, new BatchPut());
        sendRegistry.put(EnumUtil.PutMethod.batch_merge_map_long, new BatchMergeMapLong());
        sendRegistry.put(EnumUtil.PutMethod.batch_increment_long, new BatchIncrementLong());
        sendRegistry.put(EnumUtil.PutMethod.batch_merge, new BatchMerge());


        if (ConfigUtil.contains("nebula.labrador.aerospike.list_default_length")) {
            LIST_DEFAULT_LENGTH = ConfigUtil.getInt("nebula.labrador.aerospike.list_default_length");
        } else {
            throw new LabradorException("miss labrador.properties key [nebula.labrador.aerospike.list_default_length]");
        }
    }

    public void send(EnumUtil.PutMethod putMethod, Group group) throws LabradorException {
        AbstractSendEval sendEval = sendRegistry.get(putMethod);
        if (null == sendEval) {
            throw new LabradorException("SendEval error, no " + putMethod.name() + " implement yet, send variables is " + String.join(",", group.getVariables()));
        }
//        sendEval.doEval(group);
        Env.getWriteExecutor().execute(new WriteAerospikeThread(sendEval, group));
    }

    static class BatchIncrementLong extends AbstractSendEval {

        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<Group.Item> items = group.getItems();
            List<String> codes = new ArrayList<>();
            List<Long> counts = new ArrayList<>();
            if (items.size() > 0) {
                for(Group.Item item : items) {
                    codes.add(item.getCode());
                    counts.add(Long.valueOf(String.valueOf(item.getValue())));
                }
                execute(key, group.getPutMethod().name(), Value.get(codes), Value.get(counts));
            }
        }
    }

    static class BatchMerge extends AbstractSendEval {

        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<Group.Item> items = group.getItems();
            Map<String,Long> kvs = new HashMap<>();
            String curDayHour = ThreadLocalDateUtil.formatDayHourByTimestamp(group.getTs());
            if(items.size() > 0) {
                for(Group.Item item : items) {
                    kvs.put(item.getCode(), Long.valueOf(String.valueOf(item.getValue())));
                }
            }
            execute(key, group.getPutMethod().name(), Value.get(curDayHour), Value.get(kvs));
        }
    }

    static class BatchMergeMapLong extends AbstractSendEval {

        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<Group.Item> items = group.getItems();
            List<String> codes = new ArrayList<>();
            List<List> keys = new ArrayList<>();
            List<List> values = new ArrayList<>();
            if (items.size() > 0) {
                for (Group.Item item : items) {
                    codes.add(item.getCode());
                    Map itemMap = (Map) item.getValue();
                    List itemKeys = new ArrayList(itemMap.keySet());
                    List itemValues = new ArrayList<>();
                    for (Object itemKey : itemKeys) {
                        itemValues.add(itemMap.get(itemKey));
                    }
                    keys.add(itemKeys);
                    values.add(itemValues);
                }
                execute(key, group.getPutMethod().name(), Value.get(codes), Value.get(keys), Value.get(values));
            }
        }
    }

    static class BatchPut extends AbstractSendEval {

        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<Group.Item> items = group.getItems();
            if (items.size() > 0) {
                Map<String,Object> kvs = new HashMap<>();
                for (Group.Item item : items) {
                    kvs.put(item.getCode(), item.getValue());
                }
                execute(key, group.getPutMethod().name(), Value.get(kvs));
            }
        }
    }

    static class IncrementMap extends AbstractSendEval {
        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<Group.Item> items = group.getItems();
            List<String> codes = new ArrayList<>();
            List<String> indexes = new ArrayList<>();
            if (items.size() > 0) {
                for (Group.Item item : items) {
                    codes.add(item.getCode());
                    String index = String.valueOf(group.getTransformedKv().get(item.getFunctionField()));
                    indexes.add(index);
                }
                execute(key, group.getPutMethod().name(), Value.get(codes), Value.get(indexes));
            }
        }
    }

    static class IncrementMapSecondIndex extends AbstractSendEval {

        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<Group.Item> items = group.getItems();
            List<String> codes = new ArrayList<>();
            List<String> indexes = new ArrayList<>();
            List<String> secondIndexes = new ArrayList<>();
            if (items.size() > 0) {
                for (Group.Item item : items) {
                    codes.add(item.getCode());
                    String index = String.valueOf(group.getTransformedKv().get(item.getGroupKeys().get(1)));
                    String secondIndex = String.valueOf(group.getTransformedKv().get(item.getGroupKeys().get(2)));
                    indexes.add(index);
                    secondIndexes.add(secondIndex);
                }
                execute(key, group.getPutMethod().name(), Value.get(codes), Value.get(indexes), Value.get(secondIndexes));
            }
        }
    }

    static class IncrementMapPeriod extends AbstractSendEval {

        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<Group.Item> items = group.getItems();
            List<String> codes = new ArrayList<>();
            String ts = group.getCurrentHour();
            if (items.size() > 0) {
                for (Group.Item item : items) {
                    codes.add(item.getCode());
                }
                execute(key, group.getPutMethod().name(), Value.get(ts), Value.get(codes));
            }
        }
    }

    static class PutListDistinct extends AbstractSendEval {

        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<Group.Item> items = group.getItems();
            List<String> codes = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            if (items.size() > 0) {
                for (Group.Item item : items) {
                    codes.add(item.getCode());
                    values.add(item.getValue());
                }
                execute(key, group.getPutMethod().name(), Value.get(codes), Value.get(values), Value.get(LIST_DEFAULT_LENGTH));
            }
        }
    }

    static class PutMapListDistinctPeriod extends AbstractSendEval {
        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<String> codes = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            List<Integer> periods = new ArrayList<>();
            String timeKey = ThreadLocalDateUtil.formatDayHour(new Date(group.getTs()));
            List<Group.Item> items = group.getItems();
            if (items.size() > 0) {
                for (Group.Item item : items) {
                    codes.add(item.getCode());
                    values.add(item.getValue());
                    periods.add(MAP_DEFAULT_LENGTH);
                }
                execute(key, group.getPutMethod().name(), Value.get(timeKey), Value.get(codes), Value.get(values), Value.get(periods), Value.get(LIST_DEFAULT_LENGTH));
            }
        }
    }

    static class PutMapListPeriod extends AbstractSendEval {
        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<String> codes = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            List<Integer> periods = new ArrayList<>();
            String timeKey = ThreadLocalDateUtil.formatDayHour(new Date(group.getTs()));
            List<Group.Item> items = group.getItems();
            if (items.size() > 0) {
                for (Group.Item item : items) {
                    codes.add(item.getCode());
                    values.add(item.getValue());
                    periods.add(MAP_DEFAULT_LENGTH);
                }
                execute(key, group.getPutMethod().name(), Value.get(timeKey), Value.get(codes), Value.get(values), Value.get(periods), Value.get(LIST_DEFAULT_LENGTH));
            }
        }
    }


    static class PutList_N_Eval extends AbstractSendEval {

        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<Group.Item> items = group.getItems();
            List<String> codes = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            List<Integer> limits = new ArrayList<>();

            if (items.size() > 0) {
                for (Group.Item item : items) {
                    codes.add(item.getCode());
                    values.add(item.getValue());
                    if (item.getFunctionSize() > 0) {
                        limits.add(item.getFunctionSize());
                    } else {
                        limits.add(LIST_DEFAULT_LENGTH);
                    }
                }
                execute(key, group.getPutMethod().name(), Value.get(codes), Value.get(values), Value.get(limits));
            }
        }
    }

    static class PutFirstSendEval extends AbstractSendEval {

        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            Map<String, Object> kvs = new HashMap<>();
            List<Group.Item> items = group.getItems();
            if (items.size() > 0) {
                for (Group.Item item : items) {
                    kvs.put(item.getCode(), item.getValue());
                }
                execute(key, group.getPutMethod().name(), Value.get(kvs));
            }
        }
    }

    static class IncrementSendEval extends AbstractSendEval {

        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            List<Group.Item> items = group.getItems();
            List<String> codes = new ArrayList<>();
            if (items.size() > 0) {
                for (Group.Item item : items) {
                    codes.add(item.getCode());
                }
                execute(key, group.getPutMethod().name(), Value.get(codes));
            }
        }
    }

    static class PutSendEval extends AbstractSendEval {

        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            Map<String, Object> kvs = new HashMap<>();
            List<Group.Item> items = group.getItems();
            if (items.size() > 0) {
                for (Group.Item item : items) {
                    kvs.put(item.getCode(), item.getValue());
                }
                execute(key, group.getPutMethod().name(), Value.get(kvs));
            }
        }
    }

    static class SumMapPeriod extends AbstractSendEval {
        @Override
        public void eval(Group group) {
            Key key = Env.getAerospikeDao().getKey(group);
            String ts = group.getCurrentHour();
            List<String> codes = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            List<Group.Item> items = group.getItems();
            if (items.size() > 0) {
                for (Group.Item item : group.getItems()) {
                    String code = item.getCode();
                    codes.add(item.getCode());
                    values.add(item.getValue());
                }
                execute(key, group.getPutMethod().name(), Value.get(ts), Value.get(codes), Value.get(values));
            }
        }
    }

}
