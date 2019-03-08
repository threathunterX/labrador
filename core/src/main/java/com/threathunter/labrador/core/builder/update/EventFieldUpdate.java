package com.threathunter.labrador.core.builder.update;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.core.builder.EventFieldBuilder;
import com.threathunter.labrador.core.env.Env;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.exception.UpdateException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Created by wanbaowang on 17/9/4.
 */
public class EventFieldUpdate extends BaseUpdate {

    @Override
    public void update() throws UpdateException, IOException, BuilderException, LabradorException {
        String content = parseContent();
        if(StringUtils.isBlank(content)) {
            throw new LabradorException("field content is empty");
        }
        EventFieldBuilder fieldBuilder = new EventFieldBuilder(content);
        Map<String, Map<String, String>> eventFieldMap = fieldBuilder.builder();
        Env.getEventFieldContainer().update(eventFieldMap);
    }

    @Override
    public String getUrl() {
        return ConfigUtil.getString("nebula.labrador.update.event_field_define.url");
    }

    @Override
    public String getFilePath() {
        return ConfigUtil.getString("nebula.labrador.update.event_field_define.file");
    }

    public static void main(String[] args) throws LabradorException, IOException, BuilderException, UpdateException {
        EventFieldUpdate update = new EventFieldUpdate();
        update.update();
    }
}
