package com.threathunter.labrador.core.builder.idgenerator;

import com.threathunter.labrador.core.exception.LabradorException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wanbaowang on 17/8/24.
 */
public class FileVariableIdGenerator implements VariableIdGenerator {

    private String path;
    private int maxId;
    //格式, 变量名:id
    private Map<String, Integer> variableNameMappings = new HashMap<>();
    private Map<Integer, String> variableIdMappings = new HashMap<>();
    private File file;

    public FileVariableIdGenerator(String path) throws Exception {
        this.path = path;
        file = new File(path);
        File parentDir = file.getParentFile();
        if(!parentDir.exists()) {
            parentDir.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        List<String> contents = IOUtils.readLines(new FileReader(file));
        for (String content : contents) {
            if (content.contains(":")) {
                String[] items = content.split(":");
                String variableName = items[0].trim();
                int id = Integer.valueOf(items[1].trim());
                if (id > maxId) {
                    maxId = id;
                }
                this.variableNameMappings.put(variableName, id);
                this.variableIdMappings.put(id, variableName);
            } else {
                throw new LabradorException("restore variable from " + this.path + " failed, line " + content + " illegal");
            }
        }
    }

    @Override
    public boolean containsId(String name) {
        return variableNameMappings.containsKey(name);
    }

    @Override
    public int generateId(String name) throws IOException {
        if (containsId(name)) {
            return variableNameMappings.get(name);
        }
        String newLine = String.format("%s:%s", name, this.maxId + 1);
        FileWriter writer = new FileWriter(path, true);
        writer.write(newLine);
        writer.write(System.getProperty("line.separator"));
        writer.close();
        this.maxId = this.maxId + 1;
        this.variableNameMappings.put(name, this.maxId);
        this.variableIdMappings.put(this.maxId, name);
        return this.maxId;
    }

    @Override
    public String loadName(int id) {
        if(this.variableIdMappings.containsKey(id)) {
            return this.variableIdMappings.get(id);
        } else {
            return null;
        }
    }

    @Override
    public int loadId(String name) {
        return variableNameMappings.containsKey(name) ? variableNameMappings.get(name) : -1;
    }

}
