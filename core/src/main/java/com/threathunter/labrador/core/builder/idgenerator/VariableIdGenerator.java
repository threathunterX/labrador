package com.threathunter.labrador.core.builder.idgenerator;

import java.io.IOException;

/**
 * Created by wanbaowang on 17/8/24.
 */
public interface VariableIdGenerator {

    public boolean containsId(String name);

    public int generateId(String name) throws IOException;

    public String loadName(int id);

    public int loadId(String name);

}
