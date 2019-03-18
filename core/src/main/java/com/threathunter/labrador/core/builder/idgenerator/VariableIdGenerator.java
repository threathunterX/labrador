package com.threathunter.labrador.core.builder.idgenerator;

import java.io.IOException;

/**
 * 
 */
public interface VariableIdGenerator {

    public boolean containsId(String name);

    public int generateId(String name) throws IOException;

    public String loadName(int id);

    public int loadId(String name);

}
