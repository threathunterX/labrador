package com.threathunter.labrador.core.dao;

import com.aerospike.client.Key;
import com.aerospike.client.Language;
import com.threathunter.labrador.aerospike.AerospikeUtil;
import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.common.util.Constant;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.transform.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class AerospikeDao {

    private SendEvalGroups sendEvalGroups;

    public AerospikeDao() throws LabradorException {
        AerospikeUtil.register(AerospikeDao.class.getClassLoader(), "profile_write.lua", "profile_write.lua", Language.LUA);
        sendEvalGroups = new SendEvalGroups();
    }

    public void send(Group group) throws LabradorException {
        sendEvalGroups.send(group.getPutMethod(), group);
    }

    public String getSetNames(EnumUtil.Dimension dimension) throws LabradorException {
        if (dimension == EnumUtil.Dimension.uid) {
            return "uids";
        } else if (dimension == EnumUtil.Dimension.did) {
            return "dids";
        } else if (dimension == EnumUtil.Dimension.ip) {
            return "ips";
        } else if (dimension == EnumUtil.Dimension.others) {
            return "others";
        } else if(dimension == EnumUtil.Dimension.global) {
            return "global";
        } else {
            throw new LabradorException("no set name for dimension " + dimension.name());
        }
    }

    public Key getKey(Group group) {
        String namespace = group.getNamespace();
        String setName = group.getSetName();
        Key key = new Key(namespace, setName, group.getPk());
        return key;
    }

    public String getNamespace() {
        return ConfigUtil.getString(Constant.AEROSPIKE_NAMESPACE_KEY);
    }

}
