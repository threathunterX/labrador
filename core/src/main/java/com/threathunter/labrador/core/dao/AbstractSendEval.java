package com.threathunter.labrador.core.dao;

import com.aerospike.client.Key;
import com.aerospike.client.Value;
import com.threathunter.labrador.aerospike.AerospikeUtil;
import com.threathunter.labrador.core.transform.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public abstract class AbstractSendEval {
    private static final Logger logger = LoggerFactory.getLogger(AbstractSendEval.class);

    public void doEval(Group group) {
        eval(group);
    }

    public abstract void eval(Group group);

    public void execute(Key key, String method, Value... values) {
        try {
            AerospikeUtil.writeByUdf(key, "profile_write", method, values);
        } catch (Exception e) {
            StringBuilder valueBuilder = new StringBuilder();
            for(Value value : values) {
                valueBuilder.append(value.toString());
            }
            logger.error("send aerospike error, key: {} ,  value:{} ", key.toString() , valueBuilder.toString(), e);
            e.printStackTrace();
        }
    }
}
