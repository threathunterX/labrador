package com.threathunter.labrador.core.builder;

import com.threathunter.labrador.common.exception.BuilderException;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class EventFieldBuilder {

    private String text;

    private static Logger log = LoggerFactory.getLogger(EventFieldBuilder.class);

    public EventFieldBuilder(String text) {
        this.text = text;
    }

    private Map<String, String> getBasicField() {
        Map<String, String> basicFields = new HashMap<>();
        basicFields.put("id", "string");
        basicFields.put("pid", "string");
        basicFields.put("timestamp", "long");
        basicFields.put("c_ip", "string");
        basicFields.put("sid", "string");
        basicFields.put("uid", "string");
        basicFields.put("did", "string");
        basicFields.put("platform", "string");
        return basicFields;
    }

    public Map<String, Map<String, String>> builder() throws BuilderException {
        Map<String, Map<String, String>> eventFieldMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> root = mapper.reader(Map.class).readValue(this.text);
            if (!root.containsKey("values")) {
                throw new BuilderException("Builder event field error, content miss [values] attribute");
            }
            List<Object> values = (List) root.get("values");
            for (Object value : values) {
                Map mapValue = (Map) value;
                String sourceName = mapValue.get("name").toString();
                if (mapValue.containsKey("properties")) {
                    List properties = (List) mapValue.get("properties");
                    for (Object property : properties) {
                        Map propertyMap = (Map) property;
                        String name = (String) propertyMap.get("name");
                        String type = (String) propertyMap.get("type");
                        eventFieldMap.computeIfAbsent(sourceName, k -> new HashMap<>()).put(name, type);
                    }
                }
            }

            //针对每一个场景，新增一个ip域，取c_ip值。
            for (Map.Entry<String, Map<String, String>> entry : eventFieldMap.entrySet()) {
                String source = entry.getKey();
                Map<String, String> kvs = entry.getValue();
                if (kvs.containsKey("c_ip")) {
                    kvs.put("ip", kvs.get("c_ip"));
                }
                kvs.putAll(getBasicField());
                eventFieldMap.put(source, kvs);
            }
        } catch (Exception e) {
            log.error("Builder event field error ", e);
            throw new BuilderException("Builder event field error " + e.getMessage());
        }
        return eventFieldMap;
    }

}
