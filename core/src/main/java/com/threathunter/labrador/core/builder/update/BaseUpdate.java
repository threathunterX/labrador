package com.threathunter.labrador.core.builder.update;

import com.threathunter.labrador.common.exception.BuilderException;
import com.threathunter.labrador.common.util.ConfigUtil;
import com.threathunter.labrador.common.util.EnumUtil;
import com.threathunter.labrador.common.util.HttpUtils;
import com.threathunter.labrador.common.util.IOUtil;
import com.threathunter.labrador.core.exception.LabradorException;
import com.threathunter.labrador.core.exception.UpdateException;

import java.io.IOException;

/**
 * Created by wanbaowang on 17/8/24.
 */
public abstract class BaseUpdate {

    public abstract void update() throws UpdateException, IOException, BuilderException, LabradorException;

    public abstract String getUrl();

    public abstract String getFilePath();

    public String parseContent() throws IllegalArgumentException, IOException {
        String _updateType = ConfigUtil.getString("nebula.labrador.update.type");
        EnumUtil.UpdateType updateType = EnumUtil.UpdateType.valueOf(_updateType);
        if (updateType == EnumUtil.UpdateType.file) {
            String filePath = getFilePath();
            return IOUtil.readFileAsString(filePath);
        } else {
            String url = getUrl();
            return HttpUtils.get(url);
        }
    }

}
