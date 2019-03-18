package com.threathunter.labrador.core.builder.update;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.common.model.Strategy;
import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.exception.UpdateException;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class StrategyUpdate extends BaseUpdate {

    private static final Logger logger = LoggerFactory.getLogger(StrategyUpdate.class);

    @Override
    public void update() throws UpdateException, IOException, BuilderException, LabradorException {
        String content = parseContent();
        if(StringUtils.isBlank(content)) {
            throw new LabradorException("strategy content is empty");
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> root = mapper.reader(Map.class).readValue(content);
        if (!root.get("status").toString().equals("200")) {
            throw new BuilderException("get strategy failed, content " + content);
        }
        List<Map<String, Object>> items = (List) root.get("values");
        if (items.isEmpty()) {
            throw new RuntimeException("parse strategy failed, content doesn't has values element");
        }

        for (Map<String, Object> item : items) {
            Strategy strategy = Strategy.parseStrategy(item);
            Env.getStrategyContainer().putStrategy(strategy);
        }
    }

    @Override
    public String getUrl() {
        return ConfigUtil.getString("nebula.labrador.update.strategy_define.url");
    }

    @Override
    public String getFilePath() {
        return ConfigUtil.getString("nebula.labrador.update.strategy.file");
    }
}
